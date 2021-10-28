# QueryDSL 설정

## queryDSL plugin 설정
**Cache되지 않아, Build시 매번 새롭게 생성**
```groovy
plugins {
    id 'org.springframework.boot' version '2.4.13-SNAPSHOT'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10" //QueryDSL 플러그인 (Qclass 생성 역할)
    id 'java'
    
    //---생략--- 
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'com.querydsl:querydsl-jpa' //QueryDSL 라이브러리
    }
    
    //QueryDSL 설정, queryDSL plugin 설정과 맞물리면서 QClass생성 
    def querydslDir = "$buildDir/generated/querydsl" //Qclass 생성 디렉토리 지정
    
    querydsl{
        jpa = true //jpa사용
        querydslSourceDir = querydslDir //사용 할 경로
    }
   //sourceSets는 Gralde에서 하나 이상의 Java SourceDirectory를 지정하는데에 사용된다.
    sourceSets{
        main.java.srcDir querydslDir
    }
   //A Configuration represents a group of artifacts and their dependencies.(의존성의 그룹)
    configurations{
        querydsl.extendsFrom compileClasspath //compileClassPath를 상속받아 사용
    }
    compileQuerydsl{
        options.annotationProcessorPath = configurations.querydsl
    }
}
```

## annotationProcessor설정 
```groovy
ext {
    set('queryDSL', '4.3.1')
}

dependencies {
    // QueryDSL
    implementation "com.querydsl:querydsl-jpa:${queryDSL}"
    annotationProcessor "com.querydsl:querydsl-apt:${queryDSL}:jpa"
    annotationProcessor 'org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final'
    annotationProcessor 'javax.annotation:javax.annotation-api:1.3.2'
    ...
}
```

### 의존성
1. querydsl-apt
   - Qclass Generation
2. querydsl-jpa
   - application query작성 시 필요한 의존성 