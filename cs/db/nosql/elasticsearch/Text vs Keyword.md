# Text vs Keyword
- 2가지 모두 문자열 타입 Mapping에 사용된다.
- Dynamic Mapping 시, 문자열은 기본적으로 Text타입으로 매핑된다.
  - 추가적으로 [문자열].keyword 필드가 생긴다.
- 문자열의 특성에 따라서, 정적매핑을 수행하는 것이 성능에 도움이된다.

## Text
- 전문검색(FullTextSearch)을 위해 토큰이 생성된다.
- ```shell
    # REQUEST
    GET /_analyze
    {
        "analyzer": "standard",
        "text": "I am a boy"
    }
    # RESPONSE  
    
    {
        "tokens": [
            {
                "token": "i",
                "start_offset": 0,
                "end_offset": 1,
                "type": "<ALPHANUM>",
                "position": 0
            },
            {
                "token": "am",
                "start_offset": 2,
                "end_offset": 4,
                "type": "<ALPHANUM>",
                "position": 1
            },
            {
                "token": "a",
                "start_offset": 5,
                "end_offset": 6,
                "type": "<ALPHANUM>",
                "position": 2
            },
            {
                "token": "boy",
                "start_offset": 7,
                "end_offset": 10,
                "type": "<ALPHANUM>",
                "position": 3
            }
        ]   
      }     
  ```

# Keyword
- ExactMatchig을 위해서 토큰이 생성된다.
  - ```shell
      # REQUEST
      GET /_analyze
      {
          "analyzer": "keyword",
          "text": "I am a boy"
      }
      # RESPONSE  
      {
          "tokens": [
              {
                  "token": "I am a boy",
                  "start_offset": 0,
                    "end_offset": 10,    
                    "type": "word",
                    "position": 0
              }
          ]
     }         
```