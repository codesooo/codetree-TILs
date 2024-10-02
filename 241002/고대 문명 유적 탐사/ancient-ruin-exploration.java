import java.util.*;
import java.io.*;


public class Main {
    private static final int MAX_LEN = 5;
    private static final int CHOICE_LEN = 3;

    private static final int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};

    private static int ans;
    private static int K, M;

    private static Queue<Integer> numList = new LinkedList<>();

    private static class Board {
        int[][] map = new int[MAX_LEN][MAX_LEN];

        // 생성자 (2차원 배열 초기화)
        public Board() {
            for (int i = 0; i < MAX_LEN; i++) {
                for (int j = 0; j < MAX_LEN; j++) {
                    map[i][j] = 0;
                }

            }

        }
    }


    public static void main(String[] args) throws IOException {

        // 0. 입력받기
        Board board = input();

        Board tempBoard = new Board();

        // 1. 구현
        while (K-- > 0) {
            int maxScore = 0;
            Board maxScoreBoard = null;


            for (int cnt = 1; cnt <= 3; cnt++) {
                for (int sy = 0; sy < CHOICE_LEN; sy++) {
                    for (int sx = 0; sx < CHOICE_LEN; sx++) {
                        Board rotatedBoard = rotate(board, sx, sy, cnt);
                        int score = getScore(rotatedBoard);

                        if (maxScore < score) {
                            maxScore = score;
                            maxScoreBoard = rotatedBoard;
                        }
                    }
                }
            }

            if (maxScore == 0) {
                break;
            }
            board = maxScoreBoard;

            while (true) {
                fill(board, numList);
                int newScore = getScore(board);
                if (newScore == 0) {
                    break;
                }
                maxScore += newScore;
            }


            // 2. 출력
            System.out.print(maxScore + " ");
        }


    }

    private static Board rotate(Board board, int sx, int sy, int cnt) {

        Board newBoard = new Board();
        for (int i = 0; i < MAX_LEN; i++) {
            for (int j = 0; j < MAX_LEN; j++) {
                newBoard.map[i][j] = board.map[i][j];
            }
        }

        for (int i = 0; i < cnt; i++) {
            int temp = newBoard.map[sx][sy + 2];
            newBoard.map[sx][sy + 2] = newBoard.map[sx][sy];
            newBoard.map[sx][sy] = newBoard.map[sx + 2][sy];
            newBoard.map[sx + 2][sy] = newBoard.map[sx + 2][sy + 2];
            newBoard.map[sx + 2][sy + 2] = temp;

            temp = newBoard.map[sx + 1][sy + 2];
            newBoard.map[sx + 1][sy + 2] = newBoard.map[sx][sy + 1];
            newBoard.map[sx][sy + 1] = newBoard.map[sx + 1][sy];
            newBoard.map[sx + 1][sy] = newBoard.map[sx + 2][sy + 1];
            newBoard.map[sx + 2][sy + 1] = temp;
        }

        return newBoard;
    }

    private static int getScore(Board board) {
        ans = 0;
        boolean[][] visited = new boolean[MAX_LEN][MAX_LEN];

        for (int i = 0; i < MAX_LEN; i++) {
            for (int j = 0; j < MAX_LEN; j++) {
                if (!visited[i][j]) {
                    bfs(board, visited, i, j);
                }
            }
        }
        return ans;
    }

    private static void bfs(Board board, boolean[][] visited, int x, int y) {
        visited[x][y] = true;
        Queue<int[]> queue = new LinkedList<>();
        Queue<int[]> findList = new LinkedList<>();

        queue.offer(new int[]{x, y});
        findList.offer(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] now = queue.poll();

            for (int p = 0; p < 4; p++) {
                int nx = now[0] + dx[p];
                int ny = now[1] + dy[p];

                // 범위를 벗어나거나, 이미 방문한 곳일 경우
                if (nx < 0 || ny < 0 || nx >= MAX_LEN || ny >= MAX_LEN
                        || visited[nx][ny]) {
                    continue;
                }

                if (board.map[nx][ny] == board.map[now[0]][now[1]]) {
                    visited[nx][ny] = true;
                    queue.offer(new int[]{nx, ny});
                    findList.offer(new int[]{nx, ny});
                }
            }
        }

        if (findList.size() >= 3) {
            ans += findList.size();
            while (!findList.isEmpty()) {
                int[] find = findList.poll();
                board.map[find[0]][find[1]] = 0;
            }
        }
    }

    private static void fill(Board board, Queue<Integer> queue) {
        for (int j = 0; j < MAX_LEN; j++) {
            for (int i = MAX_LEN - 1; i >= 0; i--) {
                if (board.map[i][j] == 0 && !queue.isEmpty()) {
                    board.map[i][j] = queue.poll();
                }
            }
        }

    }

    private static Board input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        Board board = new Board();

        for (int i = 0; i < MAX_LEN; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < MAX_LEN; j++) {
                board.map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            numList.offer(Integer.parseInt(st.nextToken()));
        }

        return board;
    }

}