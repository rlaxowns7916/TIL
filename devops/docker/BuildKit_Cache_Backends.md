# Docker BuildKit 캐시 백엔드

## 개요

BuildKit은 빠른 빌드를 위해 빌드 결과를 자체 내부 캐시에 자동으로 저장합니다. 또한, BuildKit은 외부 위치로 빌드 캐시를 내보내기(export)하는 것을 지원하여, 향후 빌드에서 가져올 수 있도록 합니다.

외부 캐시는 CI/CD 빌드 환경에서 필수적입니다. 이러한 환경은 일반적으로 실행 간에 영구 저장소가 거의 또는 전혀 없지만, 이미지 빌드의 런타임을 최소한으로 유지하는 것이 중요합니다.

## 지원되는 캐시 백엔드

Buildx는 다음 캐시 저장 백엔드를 지원합니다:

### 1. inline
- 빌드 캐시를 이미지에 포함시킵니다.
- 인라인 캐시는 기본 출력 결과와 동일한 위치로 푸시됩니다.
- `image` exporter에서만 작동합니다.

### 2. registry
- 빌드 캐시를 별도의 이미지에 포함시키고, 기본 출력과 분리된 전용 위치로 푸시합니다.

### 3. local
- 빌드 캐시를 로컬 파일 시스템 디렉토리에 기록합니다.

### 4. gha (GitHub Actions)
- 빌드 캐시를 [GitHub Actions cache](https://docs.github.com/en/rest/actions/cache)에 업로드합니다.
- 현재 베타 기능입니다.

### 5. s3 (AWS S3)
- 빌드 캐시를 [AWS S3 버킷](https://aws.amazon.com/s3/)에 업로드합니다.
- 현재 미공개(unreleased) 상태입니다.

### 6. azblob (Azure Blob Storage)
- 빌드 캐시를 [Azure Blob Storage](https://azure.microsoft.com/en-us/services/storage/blobs/)에 업로드합니다.
- 현재 미공개(unreleased) 상태입니다.

## 사용 방법

캐시 백엔드를 사용하려면 먼저 `--cache-to` 옵션을 사용하여 캐시를 선택한 저장소 백엔드로 내보내야 합니다. 그런 다음 `--cache-from` 옵션을 사용하여 저장소 백엔드에서 현재 빌드로 캐시를 가져옵니다.

`registry` 백엔드를 사용하는 예시:

```bash
docker buildx build --push -t <registry>/<image> \
  --cache-to type=registry,ref=<registry>/<cache-image>[,parameters...] \
  --cache-from type=registry,ref=<registry>/<cache-image>[,parameters...] .
```

## 참고 사항

- 각 캐시는 특정 위치에 기록됩니다. 위치는 이전에 캐시된 데이터를 덮어쓰지 않고 두 번 기록될 수 없습니다.
- 여러 범위의 캐시를 유지하려면(예: Git 브랜치별 캐시), 내보낸 캐시에 대해 다른 위치를 사용해야 합니다.
- 빌드 프로세스 내에 비밀 또는 자격 증명을 사용하는 경우, 전용 `--secret` 옵션을 사용하여 관리하세요. `COPY` 또는 `ARG`로 비밀을 수동으로 관리하면 자격 증명 유출이 발생할 수 있습니다.

## 여러 캐시 사용

BuildKit은 여러 캐시 exporter를 지원하므로 캐시를 둘 이상의 대상으로 푸시할 수 있습니다. 또한 원격 캐시에서 원하는 만큼 가져올 수 있습니다.

일반적인 패턴은 현재 브랜치와 메인 브랜치의 캐시를 모두 사용하는 것입니다.

```bash
docker buildx build --push -t <registry>/<image> \
  --cache-to type=registry,ref=<registry>/<cache-image>:<branch> \
  --cache-from type=registry,ref=<registry>/<cache-image>:<branch> \
  --cache-from type=registry,ref=<registry>/<cache-image>:main .
```

## 구성 옵션

### 캐시 모드 (Cache Mode)

`mode` 옵션을 사용하여 내보낸 캐시에 포함할 레이어를 정의할 수 있습니다. `inline` 캐시를 제외한 모든 캐시 백엔드에서 지원됩니다.

- `mode=min` (기본값): 결과 이미지로 내보낸 레이어만 캐시됩니다.
- `mode=max`: 중간 단계의 레이어를 포함하여 모든 레이어가 캐시됩니다.

`min` 캐시는 일반적으로 더 작아 가져오기/내보내기 시간이 단축되고 저장 비용이 줄어듭니다. `max` 캐시는 더 많은 캐시 적중 가능성을 제공합니다.

### 캐시 압축 (Cache Compression)

캐시 압축 옵션은 exporter 압축 옵션과 동일합니다. `local` 및 `registry` 캐시 백엔드에서 지원됩니다.

```bash
docker buildx build --push -t <registry>/<image> \
  --cache-to type=registry,ref=<registry>/<cache-image>,compression=zstd \
  --cache-from type=registry,ref=<registry>/<cache-image> .
```

### OCI 미디어 타입

캐시 OCI 옵션은 exporter OCI 옵션과 동일합니다. `local` 및 `registry` 캐시 백엔드에서 지원됩니다.

```bash
docker buildx build --push -t <registry>/<image> \
  --cache-to type=registry,ref=<registry>/<cache-image>,oci-mediatypes=true \
  --cache-from type=registry,ref=<registry>/<cache-image> .
```

Amazon ECR과 같은 일부 OCI 레지스트리는 이미지 인덱스 미디어 타입을 지원하지 않습니다. 이 경우 `image-manifest` 매개변수를 `true`로 설정하세요.

## 참고

- 기본 `docker` 드라이버는 `inline`, `local`, `registry`, `gha` 캐시 백엔드를 지원합니다. 단, [containerd 이미지 저장소](https://docs.docker.com/desktop/features/containerd/)가 활성화된 경우에만 지원합니다.
- 다른 캐시 백엔드를 사용하려면 다른 [드라이버](https://docs.docker.com/build/builders/drivers/)를 선택해야 합니다.
