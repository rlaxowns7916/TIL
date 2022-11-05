# JenkinsFile
- Jenkins가 수행할 작업을 명시
- Jenkins 1.642.3부터, Pipeline as a code 지원
- Pipeline은 순차적인 실행이 기본적이다.
  - parallel 블록 안에 정의하게 되면, 병렬적인 실행이 된다.

## 예시

### 1. Declarative Pipeline
- Jenkins파일을 구성하기 쉽게 만들어놓은 형식이다.
- https://www.jenkins.io/doc/book/pipeline/syntax/#declarative-pipeline
```text
Jenkinsfile (Declarative Pipeline)
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
```


### 1. pipeline
- 파이프라인 정의에 필수적이다.
- 최상위 레벨에 정의하여야 한다.
- **Section, Directives, Steps, Assignments**가 내부에 존재 할 수 있다.

### 2. section

#### [1] agent (필수)
- Jenkins의 실행환경
- Jenkins 컨트롤러에 연결하고 컨트롤러의 지시에 따라 작업을 실행하는 머신 또는 컨테이너
- pipleline 최상단에 위치해도되며, stage마다 선언해도 된다.

| Agent          | Description                               |
|:---------------|:------------------------------------------|
| Any            | 사용가능한 Agent 아무거나                          |
| none           | Global Agent 명시(x), 각 Step에 별도로 정의        |
| label          | Environment에 설정된 label로 설정                |
| docker         | Docker 환경으로 수행 (추가적인 Docker스크립트를 블록안에 정의) |
| dockerfile     | Docker file로 수행                           |

#### [2] post
- 특정 Stage 이후에 실행될 Conditional Block

| post     | Description                      |
|:---------|:---------------------------------|
| always   | 실행이 끝나고 항상 실행될 Step              |
| changed  | Previous run과 다른 Status를 가질 때 실행 |
| failure  | 실패하면 실행 될 step                   |
| unstable | test fail, code violation 등일때 실행되는 step                 |
| aborted  | 강제 중지되었을 때 실행하는 Step             |

#### [3] stages (필수)
- stage들의 모임
- 내부에 여러개의 stge들을 정의 가능하다.

#### [4] steps (필수)
- stage 내부의 실행흐름
- 여러개의 step들을 통해서 stage를 정의한다.


### 3. Directives
- pipleline의 설정과 연관된 것들이다.

#### [1] environment
- key:value 형태로 환경변수 설정이 가능하다.
- pipeline 최상단, 혹은 stage블록 내부에 정의 가능하다.
- 사용자가 정의하는 것 이외에도, Jenkins가 제공해주는 환경변수도 사용이 가능하다.
#### [2] option
- pipleline안에서 한번만 정의 가능하다.

| options                 | Description                                          |
|:------------------------|:-----------------------------------------------------|
| buildDiscarder          | 최근 파이프라인 실행의 특정 수에 대해 아티팩트 및 콘솔 출력을 유지               |
| disableConcurrentBuilds | 파이프라인의 동시 실행을 허용하지 않습니다. 공유 리소스 등에 대한 동시 액세스를 방지 한다. |
| overrideIndexTriggers   | 분기 인덱싱 트리거의 기본 처리를 재정의                               |
| skipDefaultCheckout     | 에이전트 지시문에서 기본적으로 소스 제어에서 코드 체크 아웃 건너뛰기               | 
| skipStagesAfterUnstable | 빌드 상태가 unstable이 되면 건너뛰기                             |
 | checkoutToSubdirectory  | subDirectory 까지 자동 소스 체크아웃                           |
| timeout                 | Jenkins가 파이프라인을 중지해야 하는 Timeout시간 정의                 |
| retry                   | 실패시 지정한 횟수만큼 Retry                                   |
| timestamps              | 콘솔에 모든 수행 작업 Timestamp 출력                             |

***

### 2. Scripted Pipeline
- Groovy기반
- Declarative 방식 보다 다양한 작업을 할 수 있다.
- 작성 난이도가 높다.
- https://www.jenkins.io/doc/book/pipeline/syntax/#scripted-pipeline

```groovy
node {
    stage ('clone') {
        git [git path] // git clone
    }
    dir ('script') { // clone 받은 디렉토리의 script 디렉토리로 이동
        stage ('script/execute') {
            sh './execute.sh'
        }
    }
    stage ('print') {
        try {
            echo 'doing something'
        }
        catch (exc) {
            echo 'error'
            throw
       
    }
}
```
