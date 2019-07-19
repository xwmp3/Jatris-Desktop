package Algorithm;

import java.util.*;

public class SevenBag {
    private List<Integer> sevenBag; // 模拟一个装有7种方块的袋子
    private int nextNum = 4;
    private Queue<Integer> nextPieces = new LinkedList<>(); // 存储预览方块类型的队列
    private int refreshCount;

    public SevenBag(int _nextNum) {
        sevenBag = new ArrayList<Integer>() {{
            add(0);
            add(1);
            add(2);
            add(3);
            add(4);
            add(5);
            add(6);
        }};
        this.bagShuffle();
        this.nextNum = _nextNum;
        this.refreshCount = 0;
        this.nextPiecesRefresh();
    }

    private void bagShuffle() {
        Collections.shuffle(this.sevenBag);
    }

    // 刷新预览方块队列
    private void nextPiecesRefresh() {
        if (this.refreshCount < 7 - nextNum) {
            if (nextPieces.size() == 0) {
                for (int i = 0; i < this.nextNum + 1; i++) {
                    nextPieces.offer(sevenBag.get(i));
                }
            } else {
                nextPieces.poll();
                nextPieces.offer(sevenBag.get(refreshCount + nextNum));
            }
        } else {
            if (this.refreshCount % 7 == 7 - nextNum) {
                this.bagShuffle();
            }
            nextPieces.poll();
            nextPieces.offer(sevenBag.get(refreshCount % 7));
        }
        this.refreshCount++;
    }

    // 获取新的预览方块序列
    public int[] newNextPieces() {
        int[] nextPiecesInt = new int[5];
        int i = 0;
        for (int n : this.nextPieces) {
            nextPiecesInt[i] = n;
            i++;
        }
        nextPiecesRefresh();
        return nextPiecesInt;
    }
}
