## 설명
데이터의 개수가 많을 때 SubSequence 매치를 빠르게 하는 방법을 테스트한다.

### 요약
이름에 포함된 글자의 cardinality가 높을수록 reverse index의 성능 향상이 크다.
name, keyword가 커질수록 reverse index의 성능 향상이 크다.
Reverse index setup 비용은 대략 전체 query의 100배이다. (name과 keyword를 비교하는 함수가 복잡할수록 차이가 줄어든다, `Character.toLowerCase`는 매우 느리다)

### 테스트 결과

| Searcher | Row count | Name length | Keyword length | Cardinality | Setup time | Query time |
|:---|---:|---:|---:|---:|---:|---:|
|IterateAllSearch|100|5|2|100|245 μs (417.6)|2.43 μs (4.1)|
|ReverseIndexSearch|100|5|2|100|206 μs (351.7)|0.59 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|100|5|2|10000|14 μs (158.1)|1.26 μs (13.6)|
|ReverseIndexSearch|100|5|2|10000|198 μs (2137.4)|0.09 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|100|20|3|100|16 μs (27.6)|1.43 μs (2.4)|
|ReverseIndexSearch|100|20|3|100|158 μs (263.9)|0.60 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|100|20|3|10000|20 μs (653.8)|1.41 μs (44.1)|
|ReverseIndexSearch|100|20|3|10000|5.6 ms (176032.1)|0.03 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|100|100|5|100|38 μs (23.4)|1.64 μs (1.0)|
|ReverseIndexSearch|100|100|5|100|186 μs (113.5)|2.83 μs (1.7)|
|-|-|-|-|-|-|-|
|IterateAllSearch|100|100|5|10000|19 μs (583.2)|2.85 μs (83.8)|
|ReverseIndexSearch|100|100|5|10000|352 μs (10369.3)|0.03 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|10000|5|2|100|25 μs (4.4)|346 μs (60.5)|
|ReverseIndexSearch|10000|5|2|100|6.2 ms (1077.2)|5.73 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|10000|5|2|10000|24 μs (316.7)|209 μs (2718.4)|
|ReverseIndexSearch|10000|5|2|10000|6.2 ms (80895.7)|0.08 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|10000|20|3|100|19 μs (164.8)|241 μs (2047.6)|
|ReverseIndexSearch|10000|20|3|100|5.0 ms (42217.4)|0.12 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|10000|20|3|10000|12 μs (321.0)|133 μs (3501.7)|
|ReverseIndexSearch|10000|20|3|10000|4.1 ms (107394.2)|0.04 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|10000|100|5|100|19 μs (76.5)|173 μs (684.0)|
|ReverseIndexSearch|10000|100|5|100|5.1 ms (20183.9)|0.25 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|10000|100|5|10000|23 μs (90.7)|160 μs (626.3)|
|ReverseIndexSearch|10000|100|5|10000|5.2 ms (20312.4)|0.26 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|1000000|5|2|100|24 μs (1.0)|14 ms (582.6)|
|ReverseIndexSearch|1000000|5|2|100|493 ms (20416.1)|395 μs (16.4)|
|-|-|-|-|-|-|-|
|IterateAllSearch|1000000|5|2|10000|32 μs (6.1)|13 ms (2546.3)|
|ReverseIndexSearch|1000000|5|2|10000|734 ms (137134.3)|5.36 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|1000000|20|3|100|15 μs (1.0)|15 ms (982.7)|
|ReverseIndexSearch|1000000|20|3|100|684 ms (43561.8)|883 μs (56.2)|
|-|-|-|-|-|-|-|
|IterateAllSearch|1000000|20|3|10000|21 μs (2.6)|15 ms (1820.7)|
|ReverseIndexSearch|1000000|20|3|10000|988 ms (115698.6)|8.54 μs (1.0)|
|-|-|-|-|-|-|-|
|IterateAllSearch|1000000|100|5|100|15 μs (1.0)|17 ms (1159.1)|
|ReverseIndexSearch|1000000|100|5|100|1109 ms (71714.1)|1.0 ms (65.7)|
|-|-|-|-|-|-|-|
|IterateAllSearch|1000000|100|5|10000|14 μs (1.0)|17 ms (1240.0)|
|ReverseIndexSearch|1000000|100|5|10000|1702 ms (119634.2)|14 μs (1.0)|
|-|-|-|-|-|-|-|
