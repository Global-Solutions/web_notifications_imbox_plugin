gsol-web-notifications imbox plugin
===================================

gsol-web-notifications' IMBox plugin for intra-mart Accel Platform.

* Version 2018.1

## 概要
intra-mart Accel Platform(以下、iAP)において、IMBoxのMyBoxに受信したメッセージをHTML5 WebNotificationで通知する機能です。
HTML5の技術を使用するので、対応ブラウザであれば特別な拡張機能のインストールをせずに使うことができます。
また、メッセージをpush通知で行うため、無駄な通信が少ない特徴があります。
主な技術:
* HTML5 WebNotifications(implemented in gsol-web-notifications)
* HTML5 WebSocket(implemented in gsol-web-notifications)
* HTML5 WebStorage

## ファイル構成
* doc: Javadoc
* docs: notifications-imbox.coffee's documents
* src
    * main
        * conf/gsol-websocket-taker/imbox-taker.xml: IMBoxTakerImplのplugin設定
        * java/jp/co/gsol/oss/notifications/
            * action/notifications/imbox/UserIconAction.java: notificationsに表示するicon取得サービス
            * form/notifications/imbox/UserIconForm.java
            * impl/plugin/imbox/
                * IMBoxMessageIdManager.java: 配信済みのメッセージIDを管理
                * IMBoxTakerImpl.java: WebSocketメッセージ受信イベントハンドラ
                * IMBoxTask.java: メッセージ配信イベントループ
        * public/notifications/imbox/
            * coffee/notifications-imbox.coffee: WebSocketでサーバと通信し、受信したメッセージをWebNotificationsで表示する
            * js/: coffeeをコンパイルしたjsとsourcemap
        * resources/jp/co/gsol/oss/notifications/impl/plugin/imbox/imbox.dicon: IMBoxTakerの実装を指定
    * Gruntfile.coffee: coffeeファイルコンパイル設定
    * package.json: npm依存開発パッケージ設定
* LICENSE
* README.md

## 動作環境
* gsol-web-notifications
* iAP 8.0.7 or later
* jre 1.7 or later
* Google Chrome

## インストール
1. module assembllyを設定し、ビルドパスを通す
2. ユーザモジュールとしてエクスポート
3. jugglingプロジェクトにエクスポートしたユーザモジュールを追加、convention.diconに以下を追加
    ```xml
        <initMethod name="addRootPackageName">
            <arg>"jp.co.gsol.oss.notifications"</arg>
        </initMethod>
    ```
4. 依存パッケージを追加
5. Warを作成し、deploy
6. 任意のテーマモジュールのheader.htmlに以下を記述
    ```html
    <script type='text/javascript' src='notifications/js/notifications.min.js'></script>
    <script type='text/javascript' src='notifications/imbox/js/notifications-imbox.min.js'></script>
    <script>
    jQuery(function() {
        jQuery.when(
            notifications.onLoadHandler(),
            webSockets.onLoadHandler(),
            imboxNotifications.onLoadHandler('<imart type="string" value=tenantId/>', '<imart type="string" value=userCd/>')
            )
        .done(function() {
            imboxNotifications.connect().done(function(op) {
                wsop = op;
            });
        });
    });
    ```
7. gsol-web-notifications imbox pluginを実装したテーマモジュールを使用するとIMBox通知が利用できます

## 著作権および特記事項
このライブラリの著作権は、Global Solutionsが所有しています。
利用者は、GPL version 3にて、本ライブラリを使用することができます。
詳しくは、LICENSEを参照してください。
intra-mart は株式会社 NTT データ イントラマートの登録商標です。

## 連絡先
* github : https://github.com/Global-Solutions