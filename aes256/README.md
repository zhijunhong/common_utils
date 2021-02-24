# AES256加解密java语言实现

## 写在前面

基于项目安全性需要，有时候我们的项目会使用AES 256加解密算法。以下，是针对实现AES256 Padding7加密算法实现的关键步骤解析以及此过程遇到的一些问题总结。

GitHub链接地址：https://github.com/zhijunhong/common_utils/tree/master/aes256

## 一些概念

### 对称加密算法

加密和解密用到的密钥是相同的，这种加密方式加密速度非常快，适合经常发送数据的场合；缺点是密钥的传输比较麻烦。

### 非对称加密算法

加密和解密用的密钥是不同的，这种加密方式是用数学上的难解问题构造的，通常加密解密的速度比较慢，适合偶尔发送数据的场合；优点是密钥传输方便。常见的非对称加密算法为RSA、ECC和EIGamal等。

实际应用中，一般是通过RSA加密AES的密钥，传输到接收方，接收方解密得到AES密钥，然后发送方和接收方用AES密钥来通信。

## 关于AES 256

高级加密标准(AES,Advanced Encryption Standard)为最常见的对称加密算法。对称加密算法：简单来说就是加密和解密过程中使用的秘钥（根据一定的规则生成）是相同的。

![img]https://github.com/zhijunhong/common_utils/blob/master/art/20210224001.png

下面简单介绍下各个部分的作用与意义：

| 明文P           | 需要加密的明文                                               |
| --------------- | ------------------------------------------------------------ |
| 密钥K           | 用来加密明文的密码，在对称加密算法中，加密与解密的密钥是相同的。密钥为接收方与发送方协商产生，但不可以直接在网络上传输，否则会导致密钥泄漏，通常是通过非对称加密算法加密密钥，然后再通过网络传输给对方，或者直接面对面商量密钥。密钥是绝对不可以泄漏的，否则会被攻击者还原密文，窃取机密数据 |
| **AES加密算法** | **设AES加密函数为E，则 C = E(K, P),其中P为明文，K为密钥，C为密文。也就是说，把明文P和密钥K作为加密函数的参数输入，则加密函数E会输出密文C** |
| 密文C           | 经加密函数处理后，可以在网络传输中传递的密文数               |
| **AES解密算法** | **设AES解密函数为D，则 P = D(K, C),其中C为密文，K为密钥，P为明文。也就是说，把密文C和密钥K作为解密函数的参数输入，则解密函数会输出明文P** |

## AES 256加解密算法实现

本博客重点讲解AES 256加解密算法实现过程；有关AES算法原理部分，网上有很多相关的博客，这里不再赘述。这里要特别指出的一点是，AES 256中的256指的是*秘钥K*的长度，常见的密钥长度还有128位、192位。密钥的长度不同，推荐加密轮数也不同，如下表所示：

| AES     | 密钥长度（32位比特字) | 分组长度(32位比特字) | 加密轮数 |
| ------- | --------------------- | -------------------- | -------- |
| AES-128 | 4                     | 4                    | 10       |
| AES-192 | 6                     | 4                    | 12       |
| AES-256 | 8                     | 4                    | 14       |

### 生成秘钥

生成秘钥的方式是需要另一端解密人员一起协定的，不同的厂商乃至不同的项目，生成秘钥的方式理论上都要是不同的。

这里只是为简单举例：

**使用`用户名username`,`密码password`和`随机数random`经过MD5加密后再经过HAS-256 hash后生成一组密钥**

```java
byte[] pkey = generatePkey("zhijunhong", "123456", "1111");

......
  
/**
     * 生成秘钥
     *
     * @param username
     * @param password
     * @param random
     * @return
     * @throws NoSuchAlgorithmException
     */
    private byte[] generatePkey(String username, String password, String random) throws NoSuchAlgorithmException {
        String mD5Str = MD5Utility.getMD5DefaultEncode(username + random + password);
        return Sha256Utils.getSHA256ByteArray(random + mD5Str);
    }
```

### 使用密钥加密明文

从步骤1获取的密钥pkey，还需要指定向量IV，这里随机指定IV为一组数据串，实际项目中，需要协定统一的IV向量来加密明文。

```java
  String base64EncryptStr = AESUtils.aesEncryptStr("我是明文 ", pkey, AESUtils.IV);    //密文

......

  /**
     * @param content 加密前原内容
     * @param iv
     * @return base64EncodeStr   aes加密完成后内容
     * @throws
     * @Title: aesEncryptStr
     * @Description: aes对称加密
     */
    public static String aesEncryptStr(String content, byte[] pkey, String iv) {
        byte[] aesEncrypt = aesEncrypt(content, pkey, iv);
        System.out.println("加密后的byte数组:" + Arrays.toString(aesEncrypt));
        String base64EncryptStr = Base64Utils.encode(aesEncrypt);
        System.out.println("加密后 base64EncodeStr:" + base64EncryptStr);
        return base64EncryptStr;
    }
```

加密方法，参数说明：

- content：待加密的明文
- pkey：上一步骤生成的加密秘钥
- iv:加密向量IV

其中，具体加密方法`aesEncrypt(String content, byte[] pkey, String IV)`如下：

```java
/**
     * @param content 需要加密的原内容
     * @param pkey    密匙
     * @param
     * @return
     */
    public static byte[] aesEncrypt(String content, byte[] pkey, String IV) {
        try {
            //SecretKey secretKey = generateKey(pkey);
            //byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skey = new SecretKeySpec(pkey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");// "算法/加密/填充"
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);//初始化加密器
            byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
            return encrypted; // 加密
        } catch (Exception e) {
            Log.i(TAG,"aesEncrypt() method error:", e);
        }
        return null;
    }
```

最后，进行一轮base64转码`String base64EncryptStr = Base64Utils.encode(aesEncrypt);`操作后，输出密文字符串base64EncryptStr。

### 使用密钥解密密文

秘钥解密的过程就是加密的逆过程，如下：解密方法

```java
  String decodeStr = AESUtils.aesDecodeStr3(base64EncryptStr, pkey, AESUtils.IV);
```

解密方法，传递三个参数：

- base64EncryptStr：base64编码过的加密密文
- pkey：秘钥（同加密秘钥）
- IV：向量

具体解密过程：先通过base64还原密文编码，再通过`aesDecode(byte[] encryptStr, byte[] pkey, String IV)`方法进行解密

```java
  /**
     * @param base64EncryptStr base64处理过的字符串
     * @param pkey    密匙
     * @param
     * @return String    返回类型
     * @throws Exception
     * @throws
     * @Title: aesDecodeStr
     * @Description: 解密 失败将返回NULL
     */
    public static String aesDecodeStr3(String base64EncryptStr, byte[] pkey, String IV) throws Exception {
        byte[] base64DecodeStr = Base64Utils.decode(base64EncryptStr);
        byte[] aesDecode = aesDecode(base64DecodeStr, pkey, IV);
        if (aesDecode == null) {
            return null;
        }
        String result;
        result = new String(aesDecode, "UTF-8");
        return result;
    }
```

其中，aesDecode(byte[] encryptStr, byte[] pkey, String IV)方法的具体实现，基本和加密过程相差不大，如下：

```java
/**
     * 解密
     *
     * @param encryptStr 解密前的byte数组
     * @param pkey    密匙
     * @param IV
     * @return result  解密后的byte数组
     * @throws Exception
     */
    public static byte[] aesDecode(byte[] encryptStr, byte[] pkey, String IV) throws Exception {
        //SecretKey secretKey = generateKey(pkey);
        //byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec skey = new SecretKeySpec(pkey, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化解密器
        byte[] result = cipher.doFinal(encryptStr);
        return result; // 解密

    }
```

通过上述一系列操作后，最后将获取的字符数组，通过`new String(aesDecode, "UTF-8")`操作，就可以将密文重新解密成明文*"我是明文"*。

```
2021-02-24 18:05:33.651 21560-21560/com.example.aes256 I/MainActivity: encryptStr: y9COgiC06V2E1CIuhJbPfg==
2021-02-24 18:05:33.652 21560-21560/com.example.aes256 I/MainActivity: decodeStr: 我是明文 
```

完整代码：https://github.com/zhijunhong/common_utils/tree/master/aes256

**最后，别忘了点一下start哟~**





# 参考

[AES加密算法的详细介绍与实现](https://blog.csdn.net/qq_28205153/article/details/55798628)







