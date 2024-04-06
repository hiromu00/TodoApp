# TODOアプリ制作

## 流れ
1.RoomでDB周りを作る
2.DIのセットアップ(Hilt使用)
3.ViewModel周りの作成

## 詰まったところ

## 勉強になった箇所

## テーブル定義
| 名前          | データタイプ |    役割    | メモ      |
|:------------|-------:|:--------:|---------| 
| id          |    int | レコード管理項目 | プライマリキー |
| title       | String | タスクのタイトル |         |
| description | String | タスクの詳細説明 |         |

## DAO定義
| 名前          |           役割 |     引数      | 返り値     |
|:------------|-------------:|:-----------:|---------| 
| insertTask  | タスクひとつをDBに保存 | DBに保存するTask | --      |
| loadALLTask |         全件取得 |     --      | DBデータ全権 |
| updateTask  |           更新 | 更新するTaskデータ | --      |
| deleteTask  |           削除 |     --      | --      |
## 参考文献
* https://developer.android.com/jetpack/androidx/releases/room?hl=ja#kts
* https://developer.android.com/training/data-storage/room/accessing-data?hl=ja