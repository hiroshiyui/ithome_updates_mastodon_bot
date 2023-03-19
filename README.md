# ithome_updates_mastodon_bot

將 [iThome](https://www.ithome.com.tw/) 內容更新 RSS feeds 自動發送至 Mastodon

Follow me @ https://g0v.social/@unofficial_ithome

# Build & Install

1. `git clone https://github.com/hiroshiyui/ithome_updates_mastodon_bot.git`
2. `cd ithome_updates_mastodon_bot`
3. Supply `app/local.properties.xml`
4. `./gradlew build`
5. `./gradlew installDist`

# Execute

1. `cd app/`
2. `./build/install/app/bin/app`

# Notices

* 本程式為個人第三方開發之應用，與電週文化事業無關，任何使用上的問題均應洽詢開發者本人。
* 本程式採用 GNU GENERAL PUBLIC LICENSE Version 3 條款授權予公眾，係為自由軟體，敬請遵守該條款所列規定。

# About

我做這個小專案有幾個原因：

1. 我已經到處評估「哪個語言、哪個框架處理 XML 方便好用不痛苦」好一陣子了，而 RSS Feeds 就是一種 XML
   文件。這次我打算拿 [Kotlin](https://kotlinlang.org/) 來試試。
2. 我的創業題目恰好需要一個 RSS Feeds fetcher & parser。
3. 我覺得 [iThome](https://www.ithome.com.tw/) 的內容在我的 filter bubble（同溫層？）當中能見度很低，很可惜這麼一個篤實報導
   IT 業界新知的媒體，於是我希望能為其推一把。
4. 我需要接案維生，希望能透過公開作品接觸到潛在雇主。
