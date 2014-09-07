## Bitocle是什么？

Bitocle是一款简单的用来查看自己托管在GitHub上的代码的安卓应用。

版本号：2.2 Release

支持安卓4.0及其以上版本设备。

## 特别说明：

 - Bitocle使用OAuth登陆GitHub，不会记录您登陆时输入的GitHub密码。

 - 在Bitocle中完成的操作不会影响您托管在GitHub上面的内容，可以理解为，它是**只读**的。

## 如何使用它呢？

我们规定打开应用看到的第一个列表为“主列表”。
 
 - 搜索框会自动补全当前输入内容，筛选出主列表中符合输入的项目，点击列表项即可快速跳转至项目。
 
 - 在搜索框中按照“用户名/项目名”的格式输入字符，并点击输入法上的“发送（回车）”键，即可将输入的项目添加到主列表。
 
 - 在“近期加星”列表中，点击列表项右侧的菜单按钮，可以选择将加星项目添加到主列表。

 - 在项目的目录树下长按列表项可以选择添加书签。

 - 在书签界面长按列表项可以选择删除书签。
 
 - 点击Overflow菜单中的“代码高亮”，可以选择5种不同的高亮模式，方便阅读代码。

 - 点击Overflow菜单中的“代码横屏”，可以在阅读代码的时候自动横屏。

 - 点击Overflow菜单中的“夜间模式”，可以降低屏幕亮度，方便夜间阅读。
 
 - Overflow菜单中的“刷新选项可以在数据加载失败的时候重载数据，但是**不建议**频繁的刷新。
 
 - 左上角的返回键**无论何时**都会返回主列表。
 
 - 屏幕下方的返回键用于返回当前位置的上一层；在点击书签项之后原有的目录树将会被新的目录树覆盖。
 
## 目前存在哪些问题？

 - 不能优雅的退出应用。涉及网络操作的线程在快速退出时可能导致下次启动时白屏，因此使用了`System.exit(0)`直接将应用kill掉，所有就没有退出动画了。

 - 代码逻辑不够完美，导致应用的流畅性可能受到影响。

 - 缺少缓存。目前我并没有找到比较好的可以实现缓存的方式，由此导致每次查看代码的时候都需要进行网络操作，效率不高。如果你有什么比较好的想法，可以联系我，联系方式见下文。
 
 - 应用程序的启动图标。有意向的同学可以考虑帮我绘制一个漂亮的图标，联系方式见下文。
 
 - 在代码高亮时WebView加载及渲染效率较低的问题。

 - 当一行代码太长，在暗色系主题下WebView会显示白边。这属于代码风格问题，通常不推荐写太长不折行的代码。有兴趣的同学可以参阅[google-styleguide](https://code.google.com/p/google-styleguide/ "google-styleguide")。
 
 - Webview加载Markdown文件有时候无法显示图片，这取决于图片的大小和网络问题。

 - 中文乱码。使用**UTF-8**编码格式能有效防止中文乱码的出现。
 
 - 部分机型的适配问题，以及其他未知Bug（边界问题）。
 
## 下一阶段打算做什么？

 - 尝试解决以上提到的Bug。
 
 - 优化代码。
 
 - 美化UI。
 
## 关于项目：

 - 项目主页：[Bitocle](https://github.com/mthli/Bitocle "Bitocle的项目主页")
 
 - 遵循协议：[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html "Apache License, Version 2.0")
 
 - 开发者：[Matthew Lee](http://mthli.github.io/ "Matthew Lee的个人博客")
 
## Thanks:

Bitocle在开发过程中使用了以下开源项目：

 - [ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh "ActionBar-PullToRefresh")
 
 - [Android-ProgressFragment](https://github.com/johnkil/Android-ProgressFragment "Android-ProgressFragment")
 
 - [commons-io](https://github.com/apache/commons-io "commons-io")
 
 - [egit-github](https://github.com/eclipse/egit-github "egit-github")
 
 - [github-markdown-css](https://github.com/sindresorhus/github-markdown-css "github-markdown-css")
 
 - [google-gson](https://code.google.com/p/google-gson/ "google-gson")
 
 - [highlight.js](https://github.com/isagalaev/highlight.js "highlight.js")
 
 - [jQuery](http://jquery.com/ "jQuery")
 
 - [okhttp](https://github.com/square/okhttp "okhttp")
 
 - [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar "SmoothProgressBar")
 
 - [SuperToasts](https://github.com/JohnPersano/SuperToasts "SuperToasts")
 
同时也要感谢支持这个项目的所有人，我会继续努力哒！
