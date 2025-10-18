# TraverseNode
- Tree에서의 Node에 대한 구조적 추상화를 제공하는 Interface
  - 구현체로는 [DefaultTraverseNode](https://github.com/naver/fixture-monkey/blob/main/fixture-monkey-api/src/main/java/com/navercorp/fixturemonkey/api/tree/DefaultTraverseNode.java)를 가진다
  - 핵심 로직 정리는 DefaultTraverseNode를 기준으로함
- 값 생성/인스턴스화 는 Context에 위임하고, ChildNode에 대한 탐색과 최소연산만 정의한다
- `값을 직접 보관·세팅하는 주체가 아니라, 트리 구조와 생성/탐색을 위한 메타데이터를 관리한다`

## 주요로직
### [1] expand
```java
@Override
	public boolean expand() {
		if (this.expandedTypeDefinition != null) {
			return false;
		}

		this.setMergedChildren(
			this.getTreeProperty().getTypeDefinitions().stream()
				.flatMap(
					typeDefinition -> {
						if (this.getTreeProperty().isContainer()) {
							return expandContainerNode(typeDefinition, this.traverseContext);
						}

						return this.generateChildrenNodes(
							typeDefinition.getResolvedProperty(),
							typeDefinition.getPropertyGenerator()
								.generateChildProperties(typeDefinition.getResolvedProperty()),
							this.nullInject,
							this.traverseContext
						).stream();
					}
				)
				.collect(Collectors.toList())
		);
		this.expandedTypeDefinition = resolvedTypeDefinition;
		return true;
	}
```
- 이미 전개된 경우 false 반환. 새 전개 시 expandedTypeDefinition = resolvedTypeDefinition.
- treeProperty.getTypeDefinitions()를 순회해 후보 타입별 자식들을 생성:
    - 컨테이너: expandContainerNode(컨테이너 크기 조작 반영 후 요소 전개)
    - 비컨테이너: PropertyGenerator.generateChildProperties → generateChildrenNodes
- children에는 “후보 타입별 자식”이 평탄화되어 담김.
- 실제 값 생성에서는 부모의 실제 타입과 호환되는 자식만 필터링해 사용.

### [2] forceExpand
```java
@Override
	public void forceExpand(TypeDefinition typeDefinition) {
		List<TraverseNode> children;
		if (this.getTreeProperty().isContainer()) {
			children = this.expandContainerNode(
				typeDefinition,
				traverseContext.withParentProperties()
			).collect(Collectors.toList());
		} else {
			children = this.generateChildrenNodes(
				typeDefinition.getResolvedProperty(),
				typeDefinition.getPropertyGenerator()
					.generateChildProperties(typeDefinition.getResolvedProperty()),
				this.nullInject,
				traverseContext.withParentProperties()
			);
		}
		this.setMergedChildren(children);
		this.expandedTypeDefinition = typeDefinition;
	}
```
- 주어진 구체 타입 정의 하나만 대상으로 자식을 재전개한다(후보들을 전부 돌지 않음).
  - TypeDefinition이 없는 경우에는 후보군들을 다시 전개한다
  - 새 자식과 기존 자식을 mergeWithNewChildren로 병합한다. 단순 재설정이 아니라 상태 보존 + 최소 변경이 목표.
- 컨테이너/비컨테이너 처리 로직은 위와 동일하며, 결과는 mergeWithNewChildren(...)로 병합.


### [3] mergeWithNewChildren
```java
private List<TraverseNode> mergeWithNewChildren(List<TraverseNode> newChildren) {
		if (this.children == null) {
			return newChildren;
		}

		boolean shrinkChildNodes = this.children.size() > newChildren.size();
		if (shrinkChildNodes) {
			return this.children.subList(0, newChildren.size());
		}

		boolean expandChildNodes = this.children.size() < newChildren.size();
		if (expandChildNodes) {
			Map<ObjectProperty, TraverseNode> existingNodesByObjectProperty = this.children.stream()
				.collect(toMap(it -> it.getMetadata().getTreeProperty().getObjectProperty(), Function.identity()));

			List<TraverseNode> concatNewChildren = new ArrayList<>();
			for (TraverseNode newChild : newChildren) {
				TraverseNode existingNode =
					existingNodesByObjectProperty.get(newChild.getMetadata().getTreeProperty().getObjectProperty());
				if (existingNode != null) {
					concatNewChildren.add(existingNode);
				} else {
					concatNewChildren.add(newChild);
				}
			}
			return concatNewChildren;
		}
		return this.children;
	}
```
- Container의 Size가 변경됐을 떄
    - ChildNode의 수가 줄어들었을 경우: 뒤부터 삭제 (subList)
    - ChildNode의 수가 늘어났을 경우: 기존과 같은 ObjectProperty Key를 갖고잇는 노드는 재사용, 없는 것은 새로 추가
- 타입이 전달 되었을 때
    - 이미 자식이 모두 Set된 상태에서 변경이 생긴다면 해당 타입의 필드 구조로 자식을 다시만들어야한다.


### [4] expandContainerNode
```java
private Stream<TraverseNode> expandContainerNode(TypeDefinition typeDefinition, TraverseContext traverseContext) {
		TreeNodeManipulator appliedContainerInfoManipulator =
			this.getAppliedTreeNodeManipulator();

		ArbitraryContainerInfo containerInfo = appliedContainerInfoManipulator != null
			? appliedContainerInfoManipulator.getContainerInfo()
			: null;

		PropertyGenerator propertyGenerator = typeDefinition.getPropertyGenerator();
		if (propertyGenerator instanceof LazyPropertyGenerator) {
			propertyGenerator = ((LazyPropertyGenerator)propertyGenerator).getDelegate();
		}

		if (propertyGenerator instanceof ElementPropertyGenerator) {
			((ElementPropertyGenerator)propertyGenerator).updateContainerInfo(containerInfo);
		}

		List<Property> elementProperties = propertyGenerator.generateChildProperties(
			typeDefinition.getResolvedProperty()
		);

		return this.generateChildrenNodes(
			typeDefinition.getResolvedProperty(),
			elementProperties,
			this.nullInject,
			traverseContext
		).stream();
	}
```
- 현재 경로에 매칭된 마지막 컨테이너 조작자(getAppliedTreeNodeManipulator)를 조회해, 있으면 그 ArbitraryContainerInfo(size)를 ElementPropertyGenerator.updateContainerInfo(...)에 주입.
- 요소 프로퍼티를 생성해 generateChildrenNodes(...)로 자식을 만든다.
- **사이즈가 정해지는 경로**
    - 실제 요소 수는 ContainerPropertyGenerator.generate(ContainerPropertyGeneratorContext)에서 ArbitraryContainerInfo를 사용해 결정된다.
    - Set/Map 등은 내부 제너레이터가 타입 특성(EnumSet 크기 상한 등)으로 추가 보정을 한다.



### [5] generateObjectNode
```java
static DefaultTraverseNode generateObjectNode(
		TreeRootProperty rootProperty,
		@Nullable Property resolvedParentProperty,
		Property property,
		@Nullable Integer propertySequence,
		double parentNullInject,
		TraverseContext context
	) {
		ContainerPropertyGenerator containerPropertyGenerator = context.getContainerPropertyGenerator(property);
		boolean container = containerPropertyGenerator != null;

		ObjectPropertyGenerator objectPropertyGenerator = context.getObjectPropertyGenerator(property);

		TreeProperty parentTreeProperty = context.getLastTreeProperty();

		ArbitraryProperty parentArbitraryProperty = parentTreeProperty != null
			? parentTreeProperty.toArbitraryProperty(parentNullInject)
			: null;

		ObjectPropertyGeneratorContext objectPropertyGeneratorContext = new ObjectPropertyGeneratorContext(
			property,
			resolveIndex(
				resolvedParentProperty,
				parentTreeProperty,
				propertySequence,
				context
			),
			parentArbitraryProperty,
			container,
			context.getPropertyNameResolver(property)
		);

		ObjectProperty objectProperty = objectPropertyGenerator.generate(objectPropertyGeneratorContext);

		List<Property> candidateProperties = context.resolveCandidateProperties(property);

		List<ObjectProperty> objectProperties =
			context.getTreeProperties().stream()
				.map(TreeProperty::getObjectProperty).collect(Collectors.toList());
		objectProperties.add(objectProperty);

		TreeNodeManipulator appliedContainerInfoManipulator = resolveAppliedContainerInfoManipulator(
			container,
			context.getTreeManipulators(),
			objectProperties
		);

		List<TypeDefinition> typeDefinitions = candidateProperties.stream()
			.map(concreteProperty -> {
				if (!container) {
					LazyPropertyGenerator lazyPropertyGenerator = context.getResolvedPropertyGenerator();

					return new DefaultTypeDefinition(
						concreteProperty,
						lazyPropertyGenerator
					);
				}

				PropertyGenerator containerElementPropertyGenerator = new ElementPropertyGenerator(
					property,
					containerPropertyGenerator,
					context.getArbitraryContainerInfoGenerator(property),
					null
				);

				LazyPropertyGenerator lazyPropertyGenerator =
					new LazyPropertyGenerator(containerElementPropertyGenerator);

				return new DefaultTypeDefinition(
					concreteProperty,
					lazyPropertyGenerator
				);
			})
			.collect(Collectors.toList());

		double nullInject = context.getNullInjectGenerator(property)
			.generate(objectPropertyGeneratorContext);

		TreeProperty treeProperty = new TreeProperty(
			objectProperty,
			container,
			typeDefinitions
		);

		TraverseContext nextTraverseContext = context.appendArbitraryProperty(treeProperty);

		DefaultTraverseNode newObjectNode = new DefaultTraverseNode(
			rootProperty,
			resolvedParentProperty,
			new CompositeTypeDefinition(typeDefinitions).getResolvedTypeDefinition(),
			treeProperty,
			nullInject,
			nextTraverseContext
		);

		if (appliedContainerInfoManipulator != null) {
			newObjectNode.getMetadata().addTreeNodeManipulator(appliedContainerInfoManipulator);
		}
		return newObjectNode;
	}

```