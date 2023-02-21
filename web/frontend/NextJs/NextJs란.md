# NextJs
- React의 SSR (ServerSideRendering)을 쉽게 구현하게 도와주는 프레임워크이다.

## 설치
```shell
npx create-next-app@latest [--typescript]
```

## 왜 NextJS를 사용하는가 - SSR (ServerSideRendering)
```text
리액트는 CSR(Client Side Rendering)이다.
CSR의 단점은 검색엔진 최적화 (SEO) 이다.

하나의 HTML파일에 js 코드만 해석하여, 화면을 구성하기 때문에
검색엔진은 알맞은 검색어를 파싱하지 못하게 된다.

SSR은 Pre-Rendering을 통해서 완성된 HTML을 가져오게 되고
사용자와 검색엔진에게 완성된 HTML을 전달 할 수 있게 된다.

React로도 SSR을 구현 할 수 있지만, 매우 복잡하기 때문에 
NextJs가 주로 사용된다.
```

## 파일 구조

### 1. pages
- 페이지를 생성한다.
  - about이라는 페이지를 만드려면, about.tsx를 생성하면 된다.
- _app.tsx에 공통되는 레이아웃을 작성한다. 
  - URL을 통해서 특정페이지에 접근하기전 통과하는 인터셉터 역할이다.
### 2. public
- 이미지 같은 정적인 asset을 저장하는 폴더이다.

### 3. styles
- style에 관한 처리를 해주는 폴더이다.
- module css는 컴포넌트 종속적으로 스타일링하기위한 것이며, 확장자 앞에 module을 붙여줘야 한다.

### 4. next.config.js
- NextJs는 WebPack을 기본 번들러로 사용한다.
- WebPack에 관한 설정을 하는 곳이다.

## PreRendering
- Client에서 JS로 Rendering 하기 전에 미리 정적 파일 (HTML)을 만들어 내는 것이다.
- JS를 Disable을 해보면, CSR과 SSR의 차이를 알 수 있다.

## Data Fetching
- React와 다르게, Next.js에서는 아래의 방법으로 데이터를 가져온다.

### 1. getStaticProps
- Static Generation으로, **빌드 할 때 데이터를 가져온다.**
  - 사용자의 요청보다 빠른시점에 렌더링이 필요 할 때
- Static Page를 캐싱 할 때 (개인화 X)

### 2. getStaticPaths
- Static Generation에 기반한다.
- PreRendering 시, 동적 라우팅을 구현한다.

### 3. getServerSideProps
- **SSR로, 요청이 있을 때, 데이터를 불러온다.** 