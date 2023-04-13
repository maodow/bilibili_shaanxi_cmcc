# bilibili_shanximobile_apk

B站 移动安卓前端

#
#### 推荐位 => 打开apk
```
包名：tv.huan.bilibili.shanxi
类名：tv.huan.bilibili.ui.welcome.WelcomeActivity
命令：adb shell am start -n tv.huan.bilibili.shanxi/tv.huan.bilibili.ui.welcome.WelcomeActivity
```

#
#### 推荐位 =>
```
包名：tv.huan.bilibili.shanxi
类名：tv.huan.bilibili.ui.welcome.WelcomeActivity
命令：adb shell am start -n tv.huan.bilibili.shanxi/tv.huan.bilibili.ui.welcome.WelcomeActivity -e type "1" -e exit "1" -e voiceId "491893"
```

#
#### 统一搜索
```
{
    "action":"tv.huan.bilibili.shanxi.open",
    "package":"tv.huan.bilibili.shanxi",
    "component":{
        "pkg":"tv.huan.bilibili.shanxi",
        "cls":"tv.huan.bilibili.ui.welcome.WelcomeActivity"
    },
    "extras":{
        "intent_type":2,
        "intent_cid":""
    }
}
```

#
#### 组件 => lib_autosize_v1.2.1_release.aar
```
/repo/AndroidAutoSize-1.2.1.zip
```

#
#### 组件 => lib_keyboard_release_20230303.aar
```
/repo/module_keyboard_20230303.zip
```

#
#### 组件 => lib_huan_heilongjiang_release_20230302.aar
```
/repo/huan_heilongjiang_20230302.zip
```

#
#### 组件 => lib_leanback_r1-2-0-alpha02-release.aar
```
https://github.com/kalu-github/androidx_aar_leanback
```

#
#### 组件 => lib_frame_mvp_release_20230313.aar
```
https://github.com/kalu-github/android-develop-frame
```

#
#### 组件 => lib_mediaplayer_ui_release_20230311.aar/lib_mediaplayer_exo_r2.18.3_ff4.4_audio_release_20230222.aar
```
https://github.com/kalu-github/module_mediaplayer
```

#
#### 机顶盒开adb方法
```
遥控器操作上下上下左右左右菜单
1.数码（M201-D、HM201）：设置-更多设置-高级（密码10086）-开启adb（密码6321）-端口31015。
2.创维（CM201-1、EV900）：设置-更多设置-选中高级一直按遥控器右键-选择下方弹出的usb调试-端口60001。
3.中兴：按住遥控器返回键（5秒以上）然后松开迅速按遥控器左键-弹出二维码-将二维码提供中兴同事，获取授权（如已授权，直接打开adb权限即可），无端口。
4.长虹：设置-更多设置-清楚缓存（一直按确定或者OK），然后直接可链接，无端口
5.朝歌：需使用朝歌软件使用，请联系朝歌同事获取。
6.易视腾：联系易视腾获取授权文件，放到U盘根目录，插入终端，端口5114。     
7.MG101：开机状态反复按黄蓝或者黄绿。弹出界面找到adb开关，开启adb即可连接，无端口。
8.新款盒子M301-h  adb在高级里，密码8288.6321
```

#
#### 测试code
```
1、测试报告每个步骤一定要保留截图

2、生产环境测试局数据参数配置（2个测试局数据）

生产-测试-增值业务包月0.02元
APP_ID：8516778006830858242、APP_KEY：c334162ba91649ca89c54f5e742d3d51、productId ：P8516947106404188161

生产-测试-增值业务包月0.03元
APP_ID：8774148295636500482、APP_KEY：87b91bd2993b4150a69f522139f5e5f8、productId ：P8774189253786812418

以上两个局数据分别订购，在初测报告中一个用来做短信回复“0000”退订，一个用户做电视端退订使用

3、测试完成后发送邮件到黄老师（huangshijie@hl.chinamobile.com）和王嘉楠（wangjianan@migu.cn）

4、《[落地省份]《[APK名称]》[厂商]-初测验收版V1.2》此报告测试通过后用来换取正式的局数据参数信息
```