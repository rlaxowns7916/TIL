# ObjectNode
- TraverseNode의 구현체 중 하나 
- Node를 대표하는 Facade/Aggregator로서 컨텍스트,부모<->자식 링크,조작 상태를 묶어 관리
  - 로직의 대부분은 Property로 갖고있는 TraverseNode에 위임하며, GenerateFixtureContext는 자식을 생성할 때, Context를 전파하는 역할을 수행한다
- [Source](https://github.com/naver/fixture-monkey/blob/main/fixture-monkey/src/main/java/com/navercorp/fixturemonkey/tree/ObjectNode.java)

## 구성요소 
- `TraverseNode (로직의 대부분을 위임)`
  - 개념적으로는 Tree에서의 동일한 Node를 의미, Child Node 확장 등 계산의 영역을 수행
- `GenerateFixtureContext`
  - ObjectNode의 값을 어떻게 만들지 총괄하는 `실행 컨텍스트`
    - 다양한 역할을 수행하지만, ObjectNode에서는 ChildNode에 새로운 Context를 넘기는 역할을 한다.
      - `목적은 전역정책(FixtureMonkeyOptions, validOnly, ...) 전파`

## 주요로직

### [1] expand
```java
	@Override
	public boolean expand() {
		if (!this.traverseNode.expand() && this.children != null) {
			return false;
		}

		this.setChildren(
			nullSafe(this.traverseNode.getChildren()).asList().stream()
				.map(it -> new ObjectNode(it, generateFixtureContext.newChildNodeContext()))
				.collect(Collectors.toList())
		);
		return true;
	}
```
- Child Node들을 초기화하는 Method
  - 자식을 확장 할 필요가없다면 false를 반환하고 얼리리턴 (이미 확장되어 있다면 그대로 둔다)
    - traverseNode의 expand()는 자식 계산이 불필요하거나, 자식(leaf)가 없다는 것을 의미
- `지연 생성`

### [2] forceExpand
```java
@Override
	public void forceExpand(TypeDefinition typeDefinition) {
		this.traverseNode.forceExpand(typeDefinition);
		this.setChildren(
			this.mergeWithNewChildren(
				nullSafe(this.traverseNode.getChildren()).asList().stream()
					.map(it -> new ObjectNode(it, generateFixtureContext.newChildNodeContext()))
					.collect(Collectors.toList())
			)
		);
	}
```
- Child Node들을 재생성 + 기존 Node와 병합 한다.
  - Container의 Size가 변경됐을 떄 
    - ChildNode의 수가 줄어들었을 경우: 뒤부터 삭제 (subList)
    - ChildNode의 수가 늘어났을 경우: 기존과 같은 ObjectProperty Key를 갖고잇는 노드는 재사용, 없는 것은 새로 추가
  - 타입이 전달 되었을 때 
    - 이미 자식이 모두 Set된 상태에서 변경이 생긴다면 해당 타입의 필드 구조로 자식을 다시만들어야한다. 
- expand는 한번 child들이 초기화 되었다면, 변경하지않는다.
- `Container의 크기 / 타입의 변화가 있어도, expand만으로는 재생성이 불가능하다`


### [3] mergeWithNewChildren
```java
private List<ObjectNode> mergeWithNewChildren(List<ObjectNode> newChildren) {
		if (this.children == null) {
			return newChildren;
		}

		boolean shrinkChildNodes = this.children.size() > newChildren.size();
		if (shrinkChildNodes) {
			return this.children.subList(0, newChildren.size());
		}

		boolean expandChildNodes = this.children.size() < newChildren.size();
		if (expandChildNodes) {
			Map<ObjectProperty, ObjectNode> existingNodesByObjectProperty = this.children.stream()
				.collect(toMap(it -> it.getMetadata().getTreeProperty().getObjectProperty(), Function.identity()));

			List<ObjectNode> concatNewChildren = new ArrayList<>();
			for (ObjectNode newChild : newChildren) {
				ObjectNode existingNode =
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