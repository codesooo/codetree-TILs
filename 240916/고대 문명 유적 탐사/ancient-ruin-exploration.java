import java.io.*;
import java.util.*;

class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {
    private static int K, M;
    private static LinkedList<Integer> wallNumbers = new LinkedList<>();
    private static int[][] map = new int[5][5];
    private static int[][] tempMap;
    private static boolean[][] visited;

    private static int[] arrX = {1, 2, 3};
    private static int[] arrY = {1, 2, 3};

    private static int[] dx8 = {-1, -1, -1, 0, 1, 1, 1, 0};
    private static int[] dy8 = {-1, 0, 1, 1, 1, 0, -1, -1};

    private static int[] dx4 = {-1, 1, 0, 0};
    private static int[] dy4 = {0, 0, -1, 1};

    private static int[] turnResults;

    private static PriorityQueue<Point> pq = new PriorityQueue<>(new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.y != o2.y) {
                return Integer.compare(o1.y, o2.y);  // 열 번호 비교
            }
            return Integer.compare(o2.x, o1.x);  // 행 번호 비교 (큰 순)
        }
    });

    public static void main(String[] args) throws IOException {
        input();
        solution();
        printResults();
    }

    private static void solution() {
        turnResults = new int[K];
        for (int turn = 0; turn < K; turn++) {
            int maxScore = 0;
            int[][] bestRotation = null;
            int bestX = 0, bestY = 0, bestAngle = 0;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int nx = arrX[i];
                    int ny = arrY[j];

                    for (int angle = 1; angle <= 3; angle++) {
                        int[][] rotated = rotate(nx, ny, angle);
                        int score = calculateScore(rotated);
                        if (score > maxScore || (score == maxScore && angle < bestAngle)) {
                            maxScore = score;
                            bestRotation = rotated;
                            bestX = nx;
                            bestY = ny;
                            bestAngle = angle;
                        }
                    }
                }
            }

            if (maxScore == 0) {
                break;  // 더 이상 유물을 획득할 수 없음
            }

            for (int i = 0; i < 5; i++) {
                System.arraycopy(bestRotation[i], 0, map[i], 0, 5);
            }

            int turnScore = processMap(bestX, bestY);
            turnResults[turn] = turnScore;
        }
    }

    private static int[][] rotate(int x, int y, int angle) {
        int[][] rotated = new int[5][5];
        for (int i = 0; i < 5; i++) {
            System.arraycopy(map[i], 0, rotated[i], 0, 5);
        }

        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            int nx = x + dx8[i];
            int ny = y + dy8[i];
            list.add(rotated[nx][ny]);
        }

        for (int i = 0; i < angle * 2; i++) {
            list.addFirst(list.removeLast());
        }

        for (int i = 0; i < 8; i++) {
            int nx = x + dx8[i];
            int ny = y + dy8[i];
            rotated[nx][ny] = list.removeFirst();
        }

        return rotated;
    }

    private static int calculateScore(int[][] rotated) {
        int score = 0;
        boolean[][] visited = new boolean[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j]) {
                    int count = bfs(rotated, visited, i, j);
                    if (count >= 3) {
                        score += count;
                    }
                }
            }
        }
        return score;
    }

    private static int processMap(int x, int y) {
        int score = 0;
        while (true) {
            int turnScore = removeAndFill();
            if (turnScore == 0) break;
            score += turnScore;
        }
        return score;
    }

    private static int removeAndFill() {
        int score = 0;
        boolean[][] visited = new boolean[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j] && map[i][j] != 0) {
                    LinkedList<int[]> list = new LinkedList<>();
                    int count = bfs(map, visited, i, j, list);
                    if (count >= 3) {
                        score += count;
                        for (int[] point : list) {
                            map[point[0]][point[1]] = 0;
                            pq.add(new Point(point[0], point[1]));
                        }
                    }
                }
            }
        }

        while (!pq.isEmpty()) {
            Point p = pq.poll();
            if (!wallNumbers.isEmpty()) {
                map[p.x][p.y] = wallNumbers.removeFirst();
            }
        }

        return score;
    }

    private static int bfs(int[][] board, boolean[][] visited, int x, int y) {
        int count = 0;
        int value = board[x][y];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});
        visited[x][y] = true;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            count++;

            for (int i = 0; i < 4; i++) {
                int nx = cur[0] + dx4[i];
                int ny = cur[1] + dy4[i];

                if (nx >= 0 && nx < 5 && ny >= 0 && ny < 5 && !visited[nx][ny] && board[nx][ny] == value) {
                    queue.add(new int[]{nx, ny});
                    visited[nx][ny] = true;
                }
            }
        }

        return count;
    }

    private static int bfs(int[][] board, boolean[][] visited, int x, int y, LinkedList<int[]> list) {
        int count = 0;
        int value = board[x][y];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});
        visited[x][y] = true;
        list.add(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            count++;

            for (int i = 0; i < 4; i++) {
                int nx = cur[0] + dx4[i];
                int ny = cur[1] + dy4[i];

                if (nx >= 0 && nx < 5 && ny >= 0 && ny < 5 && !visited[nx][ny] && board[nx][ny] == value) {
                    queue.add(new int[]{nx, ny});
                    visited[nx][ny] = true;
                    list.add(new int[]{nx, ny});
                }
            }
        }

        return count;
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            wallNumbers.add(Integer.parseInt(st.nextToken()));
        }
    }

    private static void printResults() {
        for (int i = 0; i < K; i++) {
            if (turnResults[i] > 0) {
                System.out.print(turnResults[i] + " ");
            }
        }
        System.out.println();
    }
}