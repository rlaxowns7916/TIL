# MongoDB 셸과 CRUD

## MongoDB 셸 여는 법

리눅스 셸 -> **Mongo** 입력 <br>
셸에 연결되면 MongoDB 버전과 셸 버전이 나타나면서 실행된다.

```shell
root@02df288a9b33:/# mongo
MongoDB shell version v4.4.4
connecting to: mongodb://127.0.0.1:27017/?compressors=disabled&gssapiServiceName=mongodb
Implicit session: session { "id" : UUID("21b15584-faec-4c20-b770-3cdae360e1a6") }
MongoDB server version: 4.4.4
```

***

## MongoDB에서의 데이터베이스

### Document

RDB에서의 Row

```json
{username: "kimtaejun"}
```

***

### Collection

RDB에서의 Table

***

### Database

1. RDB에서의 Database<br>
2. Database를 미리 생성하지 않아도 접근된다.
3. DataBase는 Collection을 구분하는 네임스페이스 일뿐이다.
4. 즉, 질의를 하기위해서는 원하는 대상 Document가 존재하는 Database와 Collection을 알아야한다.

```shell
> use hello 
switched to db hello
```

## Insert And Select
### Insert
**use [database]를 통해서 db에 들어가있음을 기억<br>
(앞의 db가 db이름을 의미하는 것이아닌 그냥 명령어임)**<br>

**db.[collection].insert([document])**

1. id는 unique한 값이면 정의해주기 가능, 정의해주지않으면 mongo가 알아서 정의해준다.
```javascript
> db.users.insert({username:"kimtaejun"})
WriteResult({ "nInserted" : 1 })
```

### Select

#### 1. _id
Document의 PrimaryKey역할이며, 모든 Document에 필수적이다.
unique한 값을 정의해줄 수도 있고, 정의해주지않으면 mongo가 알아서 정의해준다.

#### 2. find()
**db.[collection].find()** ( == db.[collection].find({}))<br>
1. **모두 찾기**
```javascript
> > db.users.find()
{ "_id" : ObjectId("614f2de7e34e21234de8695f"), "username" : "kimtaejun" }
{ "_id" : ObjectId("614f30bbe34e21234de86960"), "username" : "kimtaejun", "age" : 25 }
```

2. **조건 넘기기**
   
단순 검색
```javascript
> db.users.find({username:"kimtaejun"})
{ "_id" : ObjectId("614f2de7e34e21234de8695f"), "username" : "kimtaejun" }
{ "_id" : ObjectId("614f30bbe34e21234de86960"), "username" : "kimtaejun", "age" : 25 }
```
 
조건 검색<br>
db.[collection].find($연산자:[...{명령어}]) ($ 앞에 escape문자는 마크다운 특성..)<br>


```javascript
and 연산
> db.users.find({ $and :[ {username:"kimtaejun"},{age:25}]})
{ "_id" : ObjectId("614f30bbe34e21234de86960"), "username" : "kimtaejun", "age" : 25 }
```
```javascript
or 연산
> db.users.find({$or : [{age:25},{age:null}]})
{ "_id" : ObjectId("614f2de7e34e21234de8695f"), "username" : "kimtaejun" }
{ "_id" : ObjectId("614f30bbe34e21234de86960"), "username" : "kimtaejun", "age" : 25 }
```



#### 3. count()
**db.[collection].count()**
```javascript
> db.users.count()
1
```

### update
1. **set연산자**
```javascript
> db.users.update({name:"kim"},{$set:{country:"korea"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.users.find({name:"kim"})
{ "_id" : ObjectId("61532d91d2b5f8067ccbbcb4"), "name" : "kim", "country" : "korea" }
```
도큐먼트에 새로운 항목을 추가 가능하다.

2. **대체 업데이트**
```javascript
> db.users.update({name:"kim"},{age:25})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.users.find({age:25})
{ "_id" : ObjectId("61532d91d2b5f8067ccbbcb4"), "age" : 25 }
```
$set연산이 아닌, Document 2개가 나란히 인자로 주어지면, 대체된다.

### Delete
1. **데이터 삭제**
```javascript
db.users.remove({}) //Collection의 모든 Document를 지운다.
WriteResult({ "nRemoved" : 2 })
```

```javascript
db.users.remove({name:"kim"}) //조건에 해당하는 Document를 지운다.
WriteResult({ "nRemoved" : 1 })
```

2. Collection 삭제
```javascript
> db.users.drop()
true
```
Collection과 함께 내부의 Document까지 모두 삭제