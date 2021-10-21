## PermissionX用法



前段时间看到郭神的PermissionX，因为公司需求需要暂时去掉kotlin所以就，所以就抄写了一遍学习也学习一下设计理念，顺便吧注释翻译了一下，直接上PermissionX的用法吧，自己的理解就不写了文笔比较差以后有机会再写，相关链接放在末尾。

### 概述

​		简化权限申请流程，不仅是对危险权限申请进行了封装，也对特殊权限申请进行了封装；使用者不需要再处理回调onRequestPermissionsResult；同时不在需要对sdk版本进行判断。

（此插件为PermissionX的java版，用到的库为V7-28.0.0）

### 基础用法 

<u>无论那种用法都需要将需要请求的权限在AndroidManifest中声明</u>

调用案例：

```
PermissionX.init(this)
        .permissions(Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CAMERA)
        .request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, 						@NonNull List<String> deniedList) {
				 if（allGranted）{
                    //权限全部授予时执行
                }
            }
        });
```




| 方法           | 入参                 | 说明                                               |
| -------------- | -------------------- | -------------------------------------------------- |
| init()         | 上下文对象           | 传入的参数需要是FragmentActivity或Fragment子类     |
| permissions( ) | String或List<String> | 可传入多个String值并用“，”间隔或者传入一个List对象 |
| request( )     | RequestCallback      | 请求完毕的回调                                     |

request( )回调值

| 参数        | 类型         | 说明             |
| ----------- | ------------ | ---------------- |
| allGranted  | boolean      | 是否全部请求通过 |
| grantedList | List<String> | 同意的权限列表   |
| deniedList  | List<String> | 拒绝的权限列表   |

### 进阶用法

#### 用法1：


```java
PermissionX.init(this)
    .permissions(Manifest.permission.ACCESS_WIFI_STATE,)
    .onExplainRequestReason(new ExplainReasonCallback() {
        @Override
        public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> 					deniedList) {
            scope.showRequestReasonDialog(deniedList, "你拒绝了这些权限", "继续申请", "取消");
        }
    })
    .request(new RequestCallback() {
        @Override
        public void onResult(boolean allGranted, @NonNull List<String> grantedList, 					@NonNull List<String> deniedList) {
            if（allGranted）{
                //权限全部授予时执行
            }
        }
    });

```


onExplainRequestReason()方法，方法可以用于监听那些被用户拒绝，而又可以再次去申请的权限。从方法名上也可以看出来了，应该在这个方法中解释申请这些权限的原因。

当请求完所有参数时，会弹出提示框提示用户是否重新授予权限。

| 参数       | 类型         | 说明                                    |
| ---------- | ------------ | --------------------------------------- |
| scope      | ExplainScope | 可调用showRequestReasonDialog显示提示框 |
| deniedList | List<String> | 拒绝的权限列表                          |

```java
showRequestReasonDialog(List<String> permissions,String message,String 				    			positiveText,String negativeText)
```

| 参数         | 说明                                                         |
| ------------ | ------------------------------------------------------------ |
| permissions  | 需要提示的权限列表                                           |
| message      | 提示信息                                                     |
| positiveText | 再次请求按钮文本                                             |
| negativeText | 如果不传的话相当于用户必须同意申请的这些权限，否则对话框无法关闭，而如果传入的话，对话框上会有一个取消按钮，点击取消后不会重新进行权限申请，而是会把当前的申请结果回调到request()方法当中 |

当deniedList有权限没有让用户再次申请时会最终传入request的deniedList当中

#### 用法2

```java
PermissionX.init(this)
        .permissions(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA)
        .onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(@NonNull ExplainScope scope, @NonNull 								List<String> deniedList) {
                scope.showRequestReasonDialog(deniedList, "你拒绝了这些权限", "继续申请", "取				消");
            }
        })
        .onForwardToSettings(new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull 						List<String> deniedList) {
                scope.showRequestReasonDialog(deniedList,"你需要手动开启一些权限","我知道						了","不行");
            }
        })
        .request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, 				@NonNull List<String> deniedList) {

            }
        });
```

onForwardToSettings(),所有被用户选择了拒绝且不再询问的权限都会进行到这个方法中处理，拒绝的权限都记录在deniedList参数当中。参数值同onExplainRequestReason()。

用户点击确认按钮时会跳转到应用程序的设置界面，引导用户再次赋予权限。

#### 用法3

```java
PermissionX.init(this)
        .permissions(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA)
        .explainReasonBeforeRequest()
        .onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(@NonNull ExplainScope scope, @NonNull 							List<String> deniedList) {
                scope.showRequestReasonDialog(deniedList, "这些权限将要申请", "好的", "取					消");
            }
        })
        .onForwardToSettings(new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull 						List<String> deniedList) {
                scope.showRequestReasonDialog(deniedList,"手动开启一些权限","我知道了","不					行");
            }
        })
        .request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, 				@NonNull List<String> deniedList) {

            }
        });
```

explainReasonBeforeRequest()，该方法添加后onExplainRequestReason会首先在权限授予前调用，它的作用为提示用户将要申请的权限

注意点：

第一，单独使用explainReasonBeforeRequest()方法是无效的，必须配合onExplainRequestReason()方法一起使用才能起作用。这个很好理解，因为没有配置onExplainRequestReason()方法，我们怎么向用户解释权限申请原因呢？

第二，在使用explainReasonBeforeRequest()方法时，如果onExplainRequestReason()方法中编写了权限过滤的逻辑，最终的运行结果可能和你期望的会不一致。

当onExplainRequestReason存在过滤器时可使用ExplainReasonCallbackWithBeforeParam回调

```java
PermissionX.init(this)
        .permissions(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAMERA)
        .explainReasonBeforeRequest()
        .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
            @Override
            public void onExplainReason(@NonNull ExplainScope scope, @NonNull 							List<String> deniedList, boolean beforeRequest) {
                if (beforeRequest){
                    scope.showRequestReasonDialog(deniedList, "您需要申请这些权限", "好的", 						"取消");
                }else{
                    scope.showRequestReasonDialog(deniedList, "您拒绝了这些权限", "好的", "取						消");
                }
            }
        })
        .onForwardToSettings(new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull 						List<String> deniedList) {
                scope.showRequestReasonDialog(deniedList,"手动开启一些权限","我知道了","不					行");
            }
        })
        .request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, 				@NonNull List<String> deniedList) {

            }
        });
```

beforeRequest：当第一次触发onExplainRequestReason时为true，否则为false



[原文地址](https://blog.csdn.net/guolin_blog/article/details/106181780/)

[原文github地址](https://github.com/guolindev/PermissionX)



