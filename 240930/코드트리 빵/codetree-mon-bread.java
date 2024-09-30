import java.io.*;
import java.util.*;

class Pair {
    int x;
    int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isSame(Pair pair) {
        return this.x == pair.x && this.y == pair.y;
    }
}

public class Main {
    private static int n, m;
    private static final int INT_MAX = Integer.MAX_VALUE;
    private static final Pair EMPTY = new Pair(-1, -1);
    private static final int DIR_NUM = 4;
    private static final int MAX_M = 30;
    private static final int MAX_N = 15;

    // 0이면 빈칸, 1이면 베이스 캠프, 2이면 아무도 갈 수 없는 곳
    private static int[][] grid = new int[MAX_N][MAX_N];

    // 편의점 목록을 관리
    private static Pair[] cvsList = new Pair[MAX_M];

    // 현재 사람들의 위치를 관리
    private static Pair[] people = new Pair[MAX_M];

    // 현재 시간을 기록
    private static int currT;

    // 상좌우하 순
    private static int[] dx = new int[]{-1, 0, 0, 1};
    private static int[] dy = new int[]{0, -1, 1, 0};

    // bfs에 사용되는 변수들
    private static int[][] step = new int[MAX_N][MAX_N];
    private static boolean[][] visited = new boolean[MAX_N][MAX_N];


    public static void main(String[] args) throws IOException {
        // 1. 입력
        input();

        // 2. 구현
        for (int i = 0; i < m; i++) {
            people[i] = EMPTY;
        }

        while (true) {
            currT++;
            simulate();
            if (end()) {
                break;
            }
        }


        // 3. 출력
        System.out.println(currT);
    }

    private static void simulate() {
        // Step 1. 격자에 있는 사람들을 편의점 방향으로 1칸 이동
        for (int i = 0; i < m; i++) {

            // 아직 격자 밖에 있거나, 이미 편의점에 도착한 사람이면 패스
            if (people[i] == EMPTY || people[i].isSame(cvsList[i])) {
                continue;
            }

            // 편의점으로부터 사람의 위치까지의 최단거리를 구하는 bfs 진행
            bfs(cvsList[i]);

            int px = people[i].x, py = people[i].y;

            int minDist = INT_MAX;
            int minX = -1, minY = -1;

            // 현재 위치에서 상좌우하 중 최단거리 값이 가장 작은 곳을 고르면,
            // 그곳으로 이동하는 것이 최단거리 대로 이동하는 것이 된다.
            for (int d = 0; d < DIR_NUM; d++) {
                int nx = px + dx[d];
                int ny = py + dy[d];
                if (inRange(nx, ny) && visited[nx][ny] && minDist > step[nx][ny]) {
                    minDist = step[nx][ny];
                    minX = nx; minY = ny;
                }
            }

            // 위치를 이동시켜준다.
            people[i] = new Pair(minX, minY);
        }

        // Step 2. 편의점에 도착한 사람에 한하여 앞으로 이동 불가능하다는 표시
        // grid 값을 2로 바꿔줌
        for (int i = 0; i < m; i++) {
            if (people[i].isSame(cvsList[i])) {
                int px = people[i].x, py = people[i].y;
                grid[px][py] = 2;
            }
        }

        // Step 3. 현재 시간 currT에 대해, currT <= m을 만족하면
        // t번 사람이 베이스캠프로 이동

        if (currT > m) {
            return;
        }

        // Step 3-1. 편의점으로부터 가장 가까운 베이스캠프를 고르기 위해,
        // 편의점으로부터 시작하는 BFS 진행

        bfs(cvsList[currT - 1]);

        // Step 3-2. 편의점에서 가장 가까운 베이스캠프 선택
        int minDist = INT_MAX;
        int minx = -1, minY = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (visited[i][j] && grid[i][j] == 1 && minDist > step[i][j]) {
                    minDist = step[i][j];
                    minx = i; minY = j;
                }
            }
        }

        // 베이스캠프로 이동
        people[currT - 1] = new Pair(minx, minY);
        // 해당 베이스캠프는 앞으로 이동 불가능
        grid[minx][minY] = 2;

    }

    private static boolean end() {

        for (int i = 0; i < m; i++) {
            if (!people[i].isSame(cvsList[i])) {
                return false;
            }
        }
        return true;
    }

    // (x,y)가 격자 내에 있는 좌표인지를 판단
    private static boolean inRange(int x, int y) {
        return 0 <= x && x < n && 0 <= y && y < n;
    }

    // (x,y)로 이동이 가능한지 판단
    private static boolean canGo(int x, int y) {
        return inRange(x, y) &&     // 범위를 벗어나지 않으면서
                !visited[x][y] &&   // 방문했던 적이 없으면서
                grid[x][y] != 2;    // 이동 가능한 곳이어야 한다.
    }

    // startPos를 시작으로 하는 bfs를 진행
    // 시작점으로부터의 최단거리 결과는 step배열에 기록
    private static void bfs(Pair startPos) {
        // visited, step 값 초기화
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                visited[i][j] = false;
                step[i][j] = 0;
            }
        }
        // 초기 위치를 넣어줌
        Queue<Pair> queue = new LinkedList<>();
        int sx = startPos.x, sy = startPos.y;
        queue.add(startPos);
        visited[sx][sy] = true;
        step[sx][sy] = 0;

        while (!queue.isEmpty()) {
            // 가장 앞 원소 추출
            Pair currPos = queue.poll();

            // 인접한 칸을 보며 아직 방문하지 않은 칸을 큐에 삽입
            int x = currPos.x, y = currPos.y;
            for (int d = 0; d < DIR_NUM; d++) {
                int nx = x + dx[d];
                int ny = y + dy[d];

                if (canGo(nx, ny)) {
                    visited[nx][ny] = true;
                    step[nx][ny] = step[x][y] + 1;
                    queue.add(new Pair(nx, ny));
                }
            }
        }
    }





    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());


        // 베이스캠프 위치 저장
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                grid[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 편의점 위치 저장
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;

            // 편의점 정보 저장
            cvsList[i] = new Pair(x, y);

        }
    }

}