🚀 Java Algorithm Ultimate Visualizer
~ 複雑なアルゴリズムの「思考プロセス」を可視化する ~
Java Algorithm Ultimate Visualizer は、主要なデータ構造とアルゴリズムの挙動をリアルタイムでアニメーション表示する、Java Swingベースの統合学習ツールです。

Shutterstock
詳しく見る

🌟 プロジェクトの概要
アルゴリズムの理解において、コードを読むことと実際の挙動を結びつけるのは容易ではありません。本プロジェクトは、配列の操作、グラフの探索、迷路の解法といった抽象的な概念を動的な描画へと変換し、「アルゴリズムが今、どのデータに注目しているか」を直感的に示します。

✨ 搭載されているビジュアライザー
1. Array & Sorting (配列とソート):

Merge Sort: 分割統治法による再帰的なソート過程を、アクティブな要素をハイライトしながら表示。

Binary Search: ソート済み配列に対する高速な二分探索をステップ実行。

2. Binary Search Tree (二分探索木):

ノードの動的な追加と、木の再構成をリアルタイムに描画。

3. Graph Theory (グラフ理論):

BFS (幅優先探索): ネットワークを層状に探索していく様子を可視化。

Dijkstra (ダイクストラ法): 最短経路問題を解くためのコスト更新プロセスと、最終的な最短パスをハイライト。

4. Maze Generation & Solving (迷路):

穴掘り法: 複雑な迷路が壁を壊して作られる過程（バックトラッキング）を再現。

BFS Maze Solver: スタートからゴールまでの最短ルートを、探索の波紋とともに算出。

🛠 技術的特徴
Multi-threading: 計算ロジックを別スレッドで実行し、Thread.sleep を挟むことで、UIをフリーズさせずにアニメーションを実現。

Custom Rendering: Graphics2D を使用したスクラッチからの描画ロジック。

Data Structures: PriorityQueue（ダイクストラ用）、LinkedList（BFS用）、HashMap（隣接リスト用）など、Java標準ライブラリの適切な使い分け。

🚀 実行方法
JDK 17+ がインストールされていることを確認します。

以下のコマンドでビルドおよび実行を行います。

Bash
javac algoVisualizerUltimate/AlgoVisualizerUltimate.java
java algoVisualizerUltimate.AlgoVisualizerUltimate
🕹 操作ガイド
左パネルのボタン: 実行したいアルゴリズムを選択します。

Reset All: 全ての状態を初期化し、配列データを再生成します。

下部ログエリア: アルゴリズムの実行状態（発見された値、迷路生成完了など）がテキストで表示されます。

📜 ライセンス
MIT License

「優れたアルゴリズムは、美しい構造を持っている。」
