# Object Layout in Heap

When allocating objects, the return value is the address of classIndex. Extra slots are allocated before object data, and are used to store metadata such as the size of the object and the index of JClass in method area.

|    name    | position (in slots) |                 descriptrion                 |
| :--------: | :-----------------: | :------------------------------------------: |
|    size    |         -2          |             size of object data              |
| classIndex |         -1          | index of corresponding JClass in method area |
|    data    |     0...size-1      |                                              |

The fields of super class are positioned before those of this class.

| class  |       position       |
| :----: | :------------------: |
| Object | 1...1+sizeof(Object) |
|  ...   |         ...          |
| Parent | n-sizeof(Parent)...n |
|  This  |  n...n+sizeof(This)  |
