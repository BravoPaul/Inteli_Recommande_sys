# Inteli_Recommande_sys

linkedin 会把每个用户点击了什么，打开了哪个也页面实时的放松的服务器上，然后在通过Hadoop分析，给用户做实时的推荐。如果没有Kafka是很难做实时推荐的，只能根据用户的以往的数据做推荐。
比如像淘宝，我今天买了牙刷，然后我第二天上淘宝我搜牙膏，然后他还给我推荐牙刷，这个就很不合理啊，因为我已经买过牙刷了，还是昨天买的。所以不应该再给我推荐牙刷了，
推荐个剃须刀也比牙刷合理啊。我觉得可能美团也会有类似的情况。
还有个更加形象的例子：比如就是美团，根据以往的数据，人们到饭店吃完饭我们会到旁边的冷饮店和冷饮。假如上午天气还很好，下午突然变冷，那么人们喝冷饮的欲望会减少，
此时那网站就应该减少对冷饮的推荐。像这种根据各种因素实时改变推荐策略的数据处理系统用kafka就是一个比较好的选择。

我做了个小的Demo，可以实现实时推荐的功能。但是技术和测试环境有限，所以仅仅是为了提供一种分布式智能推荐系统的思想而已。当然可能会有很多漏洞，
但是通过这个Demo可以更加清楚对zookeeper，kafka，hadoop这些技术的有个了解。
如果什么问题希望大家多指正

先来介绍一下系统
系统分为三部分：
1.用户行为收集模块
分析用户的行为，并且建模，然后作为Producer发送给Kafka服务器
2.用户行为分析模块
作为Consumer服务器从Kafka接受数据，接受到数据后分析数据，确定推送策略，如果推送策略改变，将推送策略编号放松给Zookeeper服务器
3.推送模块
监视Zookeeper服务器推送策略节点，如果有改变，选择刚刚改变的策略来进行推送
UML如下：
![image](https://github.com/BravoPaul/Inteli_Recommande_sys/blob/master/model.png)

代码也不是很难，里面我都比较详细的注释了主要代码的用途，测试了一下，主要功能没有问题。因为我用户行为的数据的处理都很简单用一个while来搞定，但在实际的工程中经常用MapReduce来进行数据的处理。所以如果有兴趣的话，可以将他做成一个web程序，然后在web前端进行压力测试，那到的数据用MapReduce处理后再通过Kafka发送给消息的消费者，而且可以根据人们在web上不同页面操作制定不用的topic，中间的环节应该不需要有太多大的修改，最后推送的环节还需要给到Web前端，所以需要和Web架构集成一下。能力有限，如有什么好的建议请告诉我。
