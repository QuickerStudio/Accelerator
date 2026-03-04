/**
 * Copyright © 2026 Quicker Studio
 * Licensed under CC BY-NC-ND 4.0
 * 版权归原作者 Quicker Studio 所有，禁止商业使用和修改
 */
package com.english.accelerator.data

/**
 * 内置词库数据
 * 包含 100 个高频英语单词
 */
val builtInWords = listOf(
    // CET4 高频词汇 (1-50)
    Word(1, "time", "/taɪm/", "时间；次数", "Time flies when you're having fun.", 9500, "CET4", "n."),
    Word(2, "person", "/ˈpɜːrsn/", "人；身体", "She is a kind person.", 9200, "CET4", "n."),
    Word(3, "year", "/jɪr/", "年；年度", "This year has been challenging.", 9100, "CET4", "n."),
    Word(4, "way", "/weɪ/", "方法；道路", "There's more than one way to solve this.", 9000, "CET4", "n."),
    Word(5, "day", "/deɪ/", "天；白天", "Have a nice day!", 8900, "CET4", "n."),
    Word(6, "thing", "/θɪŋ/", "事情；东西", "The most important thing is to stay calm.", 8800, "CET4", "n."),
    Word(7, "man", "/mæn/", "男人；人类", "Every man has his price.", 8700, "CET4", "n."),
    Word(8, "world", "/wɜːrld/", "世界；领域", "The world is changing rapidly.", 8600, "CET4", "n."),
    Word(9, "life", "/laɪf/", "生活；生命", "Life is full of surprises.", 8500, "CET4", "n."),
    Word(10, "hand", "/hænd/", "手；帮助", "Can you give me a hand?", 8400, "CET4", "n."),

    Word(11, "part", "/pɑːrt/", "部分；角色", "This is an important part of the process.", 8300, "CET4", "n."),
    Word(12, "child", "/tʃaɪld/", "儿童；孩子", "Every child deserves a good education.", 8200, "CET4", "n."),
    Word(13, "eye", "/aɪ/", "眼睛；视力", "Keep an eye on your belongings.", 8100, "CET4", "n."),
    Word(14, "woman", "/ˈwʊmən/", "女人；妇女", "She is a strong woman.", 8000, "CET4", "n."),
    Word(15, "place", "/pleɪs/", "地方；位置", "This is a beautiful place.", 7900, "CET4", "n."),
    Word(16, "work", "/wɜːrk/", "工作；作品", "Hard work pays off.", 7800, "CET4", "n./v."),
    Word(17, "week", "/wiːk/", "星期；周", "I'll see you next week.", 7700, "CET4", "n."),
    Word(18, "case", "/keɪs/", "情况；案例", "In this case, we need more evidence.", 7600, "CET4", "n."),
    Word(19, "point", "/pɔɪnt/", "点；要点", "You have a good point.", 7500, "CET4", "n."),
    Word(20, "government", "/ˈɡʌvərnmənt/", "政府；内阁", "The government announced new policies.", 7400, "CET4", "n."),

    Word(21, "company", "/ˈkʌmpəni/", "公司；陪伴", "She works for a tech company.", 7300, "CET4", "n."),
    Word(22, "number", "/ˈnʌmbər/", "数字；号码", "What's your phone number?", 7200, "CET4", "n."),
    Word(23, "group", "/ɡruːp/", "组；团体", "We work in groups of four.", 7100, "CET4", "n."),
    Word(24, "problem", "/ˈprɑːbləm/", "问题；难题", "We need to solve this problem.", 7000, "CET4", "n."),
    Word(25, "fact", "/fækt/", "事实；真相", "The fact is, we need more time.", 6900, "CET4", "n."),
    Word(26, "good", "/ɡʊd/", "好的；善良的", "That's a good idea.", 6800, "CET4", "adj."),
    Word(27, "new", "/nuː/", "新的；新鲜的", "I bought a new phone.", 6700, "CET4", "adj."),
    Word(28, "first", "/fɜːrst/", "第一的；首先", "This is my first time here.", 6600, "CET4", "adj./adv."),
    Word(29, "last", "/læst/", "最后的；上一个", "This is the last chance.", 6500, "CET4", "adj."),
    Word(30, "long", "/lɔːŋ/", "长的；长期的", "It's a long story.", 6400, "CET4", "adj."),

    Word(31, "great", "/ɡreɪt/", "伟大的；极好的", "That's a great achievement.", 6300, "CET4", "adj."),
    Word(32, "little", "/ˈlɪtl/", "小的；少的", "Just a little bit more.", 6200, "CET4", "adj."),
    Word(33, "own", "/oʊn/", "自己的；拥有", "I have my own business.", 6100, "CET4", "adj./v."),
    Word(34, "other", "/ˈʌðər/", "其他的；另一个", "Do you have any other questions?", 6000, "CET4", "adj."),
    Word(35, "old", "/oʊld/", "老的；旧的", "This is an old tradition.", 5900, "CET4", "adj."),
    Word(36, "right", "/raɪt/", "正确的；右边的", "You're absolutely right.", 5800, "CET4", "adj./n."),
    Word(37, "big", "/bɪɡ/", "大的；重要的", "This is a big decision.", 5700, "CET4", "adj."),
    Word(38, "high", "/haɪ/", "高的；高级的", "The price is too high.", 5600, "CET4", "adj."),
    Word(39, "different", "/ˈdɪfrənt/", "不同的；各种的", "We have different opinions.", 5500, "CET4", "adj."),
    Word(40, "small", "/smɔːl/", "小的；少的", "It's a small world.", 5400, "CET4", "adj."),

    Word(41, "large", "/lɑːrdʒ/", "大的；大量的", "We need a large room.", 5300, "CET4", "adj."),
    Word(42, "next", "/nekst/", "下一个的；紧接着的", "See you next time.", 5200, "CET4", "adj."),
    Word(43, "early", "/ˈɜːrli/", "早的；早期的", "The early bird catches the worm.", 5100, "CET4", "adj./adv."),
    Word(44, "young", "/jʌŋ/", "年轻的；幼小的", "She's still young.", 5000, "CET4", "adj."),
    Word(45, "important", "/ɪmˈpɔːrtnt/", "重要的；重大的", "This is very important.", 4900, "CET4", "adj."),
    Word(46, "few", "/fjuː/", "很少的；几个", "Only a few people know this.", 4800, "CET4", "adj."),
    Word(47, "public", "/ˈpʌblɪk/", "公共的；公众的", "This is public information.", 4700, "CET4", "adj./n."),
    Word(48, "bad", "/bæd/", "坏的；严重的", "That's not a bad idea.", 4600, "CET4", "adj."),
    Word(49, "same", "/seɪm/", "相同的；同样的", "We have the same goal.", 4500, "CET4", "adj."),
    Word(50, "able", "/ˈeɪbl/", "能够的；有能力的", "I'm able to help you.", 4400, "CET4", "adj."),

    // CET6 词汇 (51-75)
    Word(51, "economy", "/ɪˈkɑːnəmi/", "经济；节约", "The economy is growing steadily.", 4300, "CET6", "n."),
    Word(52, "society", "/səˈsaɪəti/", "社会；社团", "We live in a diverse society.", 4200, "CET6", "n."),
    Word(53, "culture", "/ˈkʌltʃər/", "文化；培养", "Chinese culture has a long history.", 4100, "CET6", "n."),
    Word(54, "technology", "/tekˈnɑːlədʒi/", "技术；科技", "Technology is changing our lives.", 4000, "CET6", "n."),
    Word(55, "environment", "/ɪnˈvaɪrənmənt/", "环境；周围状况", "We must protect the environment.", 3900, "CET6", "n."),
    Word(56, "education", "/ˌedʒuˈkeɪʃn/", "教育；培养", "Education is the key to success.", 3800, "CET6", "n."),
    Word(57, "development", "/dɪˈveləpmənt/", "发展；开发", "Economic development is crucial.", 3700, "CET6", "n."),
    Word(58, "experience", "/ɪkˈspɪriəns/", "经验；经历", "Experience is the best teacher.", 3600, "CET6", "n."),
    Word(59, "opportunity", "/ˌɑːpərˈtuːnəti/", "机会；时机", "This is a great opportunity.", 3500, "CET6", "n."),
    Word(60, "challenge", "/ˈtʃælɪndʒ/", "挑战；质疑", "We face many challenges.", 3400, "CET6", "n."),

    Word(61, "benefit", "/ˈbenɪfɪt/", "利益；好处", "Exercise has many benefits.", 3300, "CET6", "n./v."),
    Word(62, "resource", "/ˈriːsɔːrs/", "资源；财力", "We need to manage our resources wisely.", 3200, "CET6", "n."),
    Word(63, "strategy", "/ˈstrætədʒi/", "策略；战略", "We need a new strategy.", 3100, "CET6", "n."),
    Word(64, "process", "/ˈprɑːses/", "过程；程序", "Learning is a continuous process.", 3000, "CET6", "n./v."),
    Word(65, "system", "/ˈsɪstəm/", "系统；制度", "The system needs improvement.", 2900, "CET6", "n."),
    Word(66, "significant", "/sɪɡˈnɪfɪkənt/", "重要的；显著的", "This is a significant achievement.", 2800, "CET6", "adj."),
    Word(67, "essential", "/ɪˈsenʃl/", "必要的；本质的", "Water is essential for life.", 2700, "CET6", "adj."),
    Word(68, "effective", "/ɪˈfektɪv/", "有效的；生效的", "This is an effective method.", 2600, "CET6", "adj."),
    Word(69, "efficient", "/ɪˈfɪʃnt/", "效率高的；有能力的", "We need a more efficient system.", 2500, "CET6", "adj."),
    Word(70, "potential", "/pəˈtenʃl/", "潜在的；可能的", "She has great potential.", 2400, "CET6", "adj./n."),

    Word(71, "fundamental", "/ˌfʌndəˈmentl/", "基本的；根本的", "This is a fundamental principle.", 2300, "CET6", "adj."),
    Word(72, "comprehensive", "/ˌkɑːmprɪˈhensɪv/", "综合的；全面的", "We need a comprehensive plan.", 2200, "CET6", "adj."),
    Word(73, "sustainable", "/səˈsteɪnəbl/", "可持续的；合理利用的", "We need sustainable development.", 2100, "CET6", "adj."),
    Word(74, "innovative", "/ˈɪnəveɪtɪv/", "创新的；革新的", "This is an innovative solution.", 2000, "CET6", "adj."),
    Word(75, "diverse", "/daɪˈvɜːrs/", "多样的；不同的", "We have a diverse team.", 1900, "CET6", "adj."),

    // TOEFL 词汇 (76-90)
    Word(76, "phenomenon", "/fəˈnɑːmɪnɑːn/", "现象；奇迹", "This is a natural phenomenon.", 1800, "TOEFL", "n."),
    Word(77, "hypothesis", "/haɪˈpɑːθəsɪs/", "假设；假说", "We need to test this hypothesis.", 1700, "TOEFL", "n."),
    Word(78, "methodology", "/ˌmeθəˈdɑːlədʒi/", "方法论；方法学", "The research methodology is sound.", 1600, "TOEFL", "n."),
    Word(79, "infrastructure", "/ˈɪnfrəstrʌktʃər/", "基础设施；基础建设", "The city needs better infrastructure.", 1500, "TOEFL", "n."),
    Word(80, "implementation", "/ˌɪmplɪmenˈteɪʃn/", "实施；执行", "The implementation phase begins next month.", 1400, "TOEFL", "n."),
    Word(81, "perspective", "/pərˈspektɪv/", "观点；透视", "From my perspective, this is correct.", 1300, "TOEFL", "n."),
    Word(82, "paradigm", "/ˈpærədaɪm/", "范例；典范", "This represents a paradigm shift.", 1200, "TOEFL", "n."),
    Word(83, "criterion", "/kraɪˈtɪriən/", "标准；准则", "What's the criterion for selection?", 1100, "TOEFL", "n."),
    Word(84, "ambiguous", "/æmˈbɪɡjuəs/", "模糊的；含糊的", "The statement is ambiguous.", 1000, "TOEFL", "adj."),
    Word(85, "arbitrary", "/ˈɑːrbɪtreri/", "任意的；武断的", "This seems like an arbitrary decision.", 900, "TOEFL", "adj."),

    Word(86, "coherent", "/koʊˈhɪrənt/", "连贯的；一致的", "The argument is coherent.", 800, "TOEFL", "adj."),
    Word(87, "explicit", "/ɪkˈsplɪsɪt/", "明确的；清楚的", "The instructions are explicit.", 700, "TOEFL", "adj."),
    Word(88, "implicit", "/ɪmˈplɪsɪt/", "含蓄的；暗示的", "There's an implicit assumption here.", 600, "TOEFL", "adj."),
    Word(89, "intrinsic", "/ɪnˈtrɪnsɪk/", "内在的；固有的", "This has intrinsic value.", 500, "TOEFL", "adj."),
    Word(90, "prevalent", "/ˈprevələnt/", "流行的；普遍的", "This view is prevalent.", 400, "TOEFL", "adj."),

    // 常用动词 (91-100)
    Word(91, "achieve", "/əˈtʃiːv/", "达到；完成", "We can achieve our goals.", 3900, "CET4", "v."),
    Word(92, "develop", "/dɪˈveləp/", "发展；开发", "We need to develop new skills.", 3800, "CET4", "v."),
    Word(93, "improve", "/ɪmˈpruːv/", "改善；提高", "We must improve our performance.", 3700, "CET4", "v."),
    Word(94, "increase", "/ɪnˈkriːs/", "增加；增长", "Sales increased by 20%.", 3600, "CET4", "v."),
    Word(95, "reduce", "/rɪˈduːs/", "减少；降低", "We need to reduce costs.", 3500, "CET4", "v."),
    Word(96, "provide", "/prəˈvaɪd/", "提供；供给", "We provide excellent service.", 3400, "CET4", "v."),
    Word(97, "require", "/rɪˈkwaɪər/", "需要；要求", "This job requires experience.", 3300, "CET4", "v."),
    Word(98, "consider", "/kənˈsɪdər/", "考虑；认为", "Please consider my proposal.", 3200, "CET4", "v."),
    Word(99, "maintain", "/meɪnˈteɪn/", "维持；保持", "We must maintain quality.", 3100, "CET6", "v."),
    Word(100, "establish", "/ɪˈstæblɪʃ/", "建立；确立", "We need to establish trust.", 3000, "CET6", "v.")
)
