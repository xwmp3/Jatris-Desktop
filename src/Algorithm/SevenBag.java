package Algorithm;

import java.util.*;

public class SevenBag {
    private List<Integer> sevenBag;
    private int nextNum = 4;
    private Queue<Integer> nextPieces = new LinkedList<>();
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
