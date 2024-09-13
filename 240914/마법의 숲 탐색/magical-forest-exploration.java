import java.util.*;
import java.io.*;
public class Main {
    private static int row, col, K;
    private static int[][] map;

    private static int[] dx = {-1, 1, 0, 0};
    private static int[] dy = {0, 0, -1, 1};

    private static int sum = 0;
    public static void main(String[] args) throws IOException {

        // 0. 입력받기
        input();

        // 1. 출력하기
        System.out.println(sum);
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        row = Integer.parseInt(st.nextToken());
        col = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[row][col];

        for (int i = 1; i <= K; i++) {
            st = new StringTokenizer(br.readLine());
            int startRow = Integer.parseInt(st.nextToken()) - 1;
            int exitNum = Integer.parseInt(st.nextToken());

            // 골렘 이동
            int[] res = move(startRow, exitNum, i);
            boolean inMap = res[0] == 1;
            int x = res[1];
            int y = res[2];

            // 골렘 몸 일부가 숲을 벗어나있는지 확인
            if (inMap) {
                // 정령 이동
                sum += bfs(x, y, i);
            } else {
                // 숲 초기화
                map = new int[row][col];
            }


        }


    }


    // 출구 위치
    private static int[] getExit(int x, int y, int exitNum) {
        switch (exitNum) {
            case 0:
                return new int[]{x - 1, y};
            case 1:
                return new int[]{x, y + 1};
            case 2:
                return new int[]{x + 1, y};
            default:
                return new int[]{x, y - 1};
        }
    }

    // 주어진 좌표가 범위 내에 있는지 확인
    private static boolean inMap(int nx, int ny) {
        return nx >= 0 && nx < row && ny >= 0 && ny < col;
    }

    // 골렘이 주어진 좌표로 이동 가능한 상태인지 확인
    private static boolean check(int x, int y) {
        if (!inMap(x, y)) {
            if (x < row && y >= 0 && y < col) {
                return true;
            }
        }
        else {
            if (map[x][y] == 0) {
                return true;
            }
        }
        return false;
    }

    // 골렘 이동, 최종 위치 반환
    private static int[] move(int startRow, int exitNum, int i) {
        int x = -2;
        int y = startRow;

        while (true) {
            // 남쪽으로 이동
            if (check(x + 2, y) && check(x + 1, y - 1) && check(x + 1, y + 1)) {
                x += 1;

            }

            // 서쪽으로 이동
            else if (check(x + 1, y - 1) && check(x-1, y -1) && check(x,y-2) && check(x + 1, y - 2) && check(x + 2, y - 1)) {
                x += 1;
                y -= 1;
                exitNum = (exitNum - 1 + 4) % 4;
            }

            // 동쪽으로 이동
            else if (check(x + 1, y + 1) && check(x - 1, y + 1) && check(x, y + 2) && check(x + 1, y + 2) && check(x + 2, y + 1)) {
                x += 1;
                y += 1;
                exitNum = (exitNum + 1) % 4;
            }

            // 골렘의 일부가 범위 밖으로 나오면 이동 중단
            else {
                break;
            }

        }

        // map에 골렘 update
        return updateMap(x, y, startRow, exitNum, i);
    }

    private static int[] updateMap(int x, int y, int startRow, int exitNum, int i) {
        // 골렘 지도에 표시
        if (!inMap(x, y) || !inMap(x + 1, y) || !inMap(x - 1, y) || !inMap(x, y + 1) || !inMap(x, y - 1)) {
            return new int[]{0, -1, -1};
        } else {
            // 골렘 번호 저장
            map[x][y] = map[x + 1][y] = map[x - 1][y] = map[x][y + 1] = map[x][y - 1] = i;

            int[] exit = getExit(x, y, exitNum);
            int ex = exit[0];
            int ey = exit[1];
            map[ex][ey] = -i;   // 출구를 '-골렘번호' 로 설정
            return new int[]{1, x, y};
        }
    }

    private static int bfs(int sx, int sy, int i) {
        List<Integer> canVisitY = new ArrayList<>();    // 정령이 방문 가능한 y좌표
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{sx, sy});
        boolean[][] visit = new boolean[row][col];
        visit[sx][sy] = true;

        while (!q.isEmpty()) {
            int[] current = q.poll();

            int x = current[0];
            int y = current[1];

            for (int p = 0; p < 4; p++) {
                int nx = x + dx[p];
                int ny = y + dy[p];
                if (!inMap(nx, ny) || visit[nx][ny] || map[nx][ny] == 0) {
                    continue;

                }

                // 같은 골렘의 부분이거나 출구인 경우 or 현재 위치가 출구이고 다음 위치가 출구가 아닌 경우
                if (Math.abs(map[x][y]) == Math.abs(map[nx][ny]) || (map[x][y] < 0 && Math.abs(map[nx][ny]) != Math.abs(map[x][y]))) {

                    q.add(new int[]{nx, ny});
                    visit[nx][ny] = true;
                    canVisitY.add(nx);
                }
            }
        }

        Collections.sort(canVisitY, Collections.reverseOrder());
        return canVisitY.get(0) + 1;
    }

}