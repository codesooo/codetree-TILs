import java.io.*;
import java.util.*;

class Info {
    int c, d;
    // c : 골렘 출발 열
    // d : 골렘 출구 방향 정보
    public Info(int c, int d) {
        this.c = c;
        this.d = d;
    }
}
public class Main {
    private static int r, c, k;
    private static int TOTAL_X;
    private static final int MAX_R = 70;
    private static final int MAX_C = 70;
    private static int[][] grid = new int[MAX_R + 3][MAX_C];
    private static int[][] exitMap = new int[MAX_R + 3][MAX_C];
    private static Queue<Info> queue = new LinkedList<>();
    private static int ans = 0;

    private static int[] dx = {-1, 1, 0, 0};
    private static int[] dy = {0, 0, -1, 1};
    public static void main(String[] args) throws IOException{
        // 1. 입력받기
        input();

        // 2. 구현
        for (int i = 1; i <= k; i++) {
            Info currentInfo = queue.poll();
            int y = currentInfo.c;
            int exitNum = currentInfo.d;
            int x = 1;


            while (true) {
                // 남쪽으로 이동
                if (checkMoveDown(x, y)) {
                    int now[] = moveDown(x, y, i);
                    x = now[0];
                    y = now[1];
                } else if (checkMoveLeft(x, y)) {    // 서쪽으로 이동
                    int now[] = moveLeft(x, y, i, exitNum);
                    x = now[0];
                    y = now[1];
                    exitNum = now[2];
                } else if (checkMoveRight(x, y)) {   // 동쪽으로 이동
                    int now[] = moveRight(x, y, i, exitNum);
                    x = now[0];
                    y = now[1];
                    exitNum = now[2];
                } else {    // 어느곳으로도 더이상 갈 수 없으면
                    break;
                }
            }


            // 골렘의 몸통이 다 들어오지 않았는지 검사
            if (!checkInGrid()) {
                for (int l = 0; l < TOTAL_X; l++) {
                    for (int m = 0; m < c; m++) {
                        grid[l][m] = 0;
                        exitMap[l][m] = 0;
                    }
                }

                continue;
            }



//             정령 이동
            ans += moveTarget(x, y, i, exitNum) -2;
//            System.out.println(ans);


        }

        // 3. 출력
        System.out.println(ans);

    }

    // 골렘 중앙 좌표 (x,y), 골렘 번호, 출구 번호
    private static int moveTarget(int x, int y, int golemNum, int exitNum) {

        // 1> 현재 골렘에서의 최대 행
        int maxRow = x + 1;

        // 2> 출구를 통해 이동하기
        // 출구 좌표 구하기
        int ex = 0, ey = 0;
        switch (exitNum) {
            case 0:
                ex = x - 1;
                ey = y;
                break;
            case 1:
                ex = x;
                ey = y+1;
                break;
            case 2:
                ex = x + 1;
                ey = y;
                break;
            case 3:
                ex = x;
                ey = y - 1;
                break;
        }

        exitMap[ex][ey] = golemNum;

        maxRow = Math.max(maxRow, bfs(ex, ey));


        return maxRow;
    }

    private static int bfs(int ex, int ey) {
        int maxRow = 0;
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[MAX_R + 3][MAX_C];
        queue.add(new int[]{ex, ey});
        visited[ex][ey] = true;

        while (!queue.isEmpty()) {
            if (maxRow == TOTAL_X - 1) {
                return maxRow;
            }
            int[] now = queue.poll();
            int x = now[0], y = now[1];



            for (int p = 0; p < 4; p++) {
                int nx = x + dx[p];
                int ny = y + dy[p];

                if (nx < 0 || ny < 0 || nx >= TOTAL_X || ny >= c || visited[nx][ny]) {
                    continue;
                }

                // 출구가 아니면
                if (exitMap[x][y] == 0) {
                    // 같은 골렘으로만 이동 가능
                    if (grid[nx][ny] > 0 && (grid[nx][ny] == grid[x][y])) {
                        visited[nx][ny] = true;
                        queue.offer(new int[]{nx, ny});
                        maxRow = Math.max(nx, maxRow);
                    }
                }
                // 출구이면
                else {
                    // 골렘 있는 곳으로 모두 이동 가능
                    if (grid[nx][ny] > 0) {
                        visited[nx][ny] = true;
                        queue.offer(new int[]{nx, ny});
                        maxRow = Math.max(nx, maxRow);
                    }
                }
            }

        }
        return maxRow;
    }



    private static boolean checkInGrid() {
        // 사용 불가능 영역에 골렘 몸통이 있으면 false를 반환
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < c; j++) {
                if (grid[i][j] > 0) {
                    return false;
                }
            }
        }
        return true;
    }


    private static boolean checkMoveDown(int x, int y) {
        if (0 > x + 1 || x + 1 >= TOTAL_X
                || 0 > y - 1 || y - 1 >= c
                || 0 > x + 2 || x + 2 >= TOTAL_X
                || 0 > y + 1 || y + 1 >= c) {
            return false;
        }

        if (grid[x + 1][y - 1] != 0 || grid[x + 2][y] != 0 || grid[x + 1][y + 1] != 0) {
            return false;
        }
        return true;
    }

    private static int[] moveDown(int x, int y, int num) {
        grid[x - 1][y] = 0;
        grid[x][y - 1] = 0;
        grid[x][y + 1] = 0;

        grid[x][y] = num;
        grid[x + 1][y - 1] = num;
        grid[x + 1][y] = num;
        grid[x + 1][y + 1] = num;
        grid[x + 2][y] = num;

        return new int[]{x+1, y};
    }

    private static boolean checkMoveLeft(int x, int y) {
        if (0 > x + 1 || x + 1 >= TOTAL_X
                || 0 > x - 1 || x - 1 >= TOTAL_X
                || 0 > y - 2 || y - 2 >= c
                || 0 > x + 2 || x + 2 >= TOTAL_X
                || 0 > y - 1 || y - 1 >= c) {
            return false;
        }
        if (grid[x + 1][y - 2] != 0 || grid[x + 1][y - 1] != 0
                || grid[x + 2][y - 1] != 0 || grid[x - 1][y - 1] != 0
                || grid[x][y - 2] != 0) {
            return false;
        }
        return true;
    }

    private static int[] moveLeft(int x, int y, int num, int exitNum) {
        grid[x - 1][y] = 0;
        grid[x][y] = 0;
        grid[x][y + 1] = 0;

        grid[x][y - 1] = num;
        grid[x + 1][y] = num;
        grid[x + 1][y - 2] = num;
        grid[x + 1][y - 1] = num;
        grid[x + 2][y - 1] = num;

        if (exitNum == 0) {
            exitNum = 3;
        } else {
            exitNum -= 1;
        }

        return new int[]{x + 1, y - 1, exitNum};
    }

    private static boolean checkMoveRight(int x, int y) {
        if (0 > x + 1 || x + 1 >= TOTAL_X
                || 0 > x + 1 || x + 1 >= TOTAL_X
                || 0 > y + 2 || y + 2 >= c
                || 0 > x + 2 || x + 2 >= TOTAL_X
                || 0 > y + 1 || y + 1 >= c) {
            return false;
        }

        if (grid[x + 1][y + 1] != 0 || grid[x + 1][y + 2] != 0
                || grid[x + 2][y + 1] != 0 || grid[x - 1][y + 1] != 0
                || grid[x][y + 2] != 0) {
            return false;
        }
        return true;
    }

    private static int[] moveRight(int x, int y, int num, int exitNum) {
        grid[x - 1][y] = 0;
        grid[x][y] = 0;
        grid[x][y - 1] = 0;

        grid[x + 1][y] = num;
        grid[x][y + 1] = num;
        grid[x + 1][y + 1] = num;
        grid[x + 1][y + 2] = num;
        grid[x + 2][y + 1] = num;

        if (exitNum == 3) {
            exitNum = 0;
        } else {
            exitNum += 1;
        }

        return new int[]{x + 1, y + 1, exitNum};
    }


    private static void input() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        r = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        TOTAL_X = r + 3;

        for (int i = 0; i < k; i++) {
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());

            queue.add(new Info(c, d));
        }
    }
}