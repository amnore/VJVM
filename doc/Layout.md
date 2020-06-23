# Object Layout in Heap

When allocating objects, the return value is the address of classIndex. Extra slots are allocated before object data, and are used to store metadata such as the size of the object and the index of JClass in method area.

|    name    | position (in slots) |                 description                  |
| :--------: | :-----------------: | :------------------------------------------: |
|    size    |         -2          |             size of object data              |
| classIndex |         -1          | index of corresponding JClass in method area |
|    data    |     0...size-1      |                                              |

The fields of super class are positioned before those of this class.

| class  |  position (inclusive)  |
| :----: | :--------------------: |
| Object |  0...sizeof(Object)-1  |
|  ...   |          ...           |
| Parent | n-sizeof(Parent)...n-1 |
|  This  |   n...n+sizeof(This)   |

The length field of an array are put before its elements.

|    name    |    position (in slots)    |                 descriptrion                 |
| :--------: | :-----------------------: | :------------------------------------------: |
|    size    |            -2             |             size of object data              |
| classIndex |            -1             | index of corresponding JClass in method area |
|   Object   |   0...sizeof(Object)-1    |               fields of Object               |
|   length   |      sizeof(Object)       |                 array length                 |
|  elements  | sizeof(Object)+1...size-1 |                                              |
