# Object Layout in Heap

|    name    | position (in slots) |                 descriptrion                 |
| :--------: | :-----------------: | :------------------------------------------: |
|     sz     |         -1          |             size of this object              |
| classIndex |          0          | index of corresponding JClass in method area |
|    data    | (from 1 to sz - 1)  |                                              |
