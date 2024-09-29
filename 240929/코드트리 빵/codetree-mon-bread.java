import java.io.*;
import java.util.*;

class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {
    private static int n, m;
    private static Position[] peopleArr;
    private static int[][] campArr;    // 편의점, 베캠 위치 저장
    private static Position[] storeArr; // 편의점 정보 저장
    private static int ans = 0; // 사람의 편의점 도착 시간 누적
    private static int time;
    private static int notStorePeople;  // 편의점에 도착하지 못한 사람 수

    // 상 좌 우 하
    private static int[] dx = {-1, 0, 0, 1};
    private static int[] dy = {0, -1, 1, 0};
    public static void main(String[] args) throws IOException {
        // 1. 입력
        input();

        // 2. 구현
        time = 0;
        while (true) {
            time++;
            // 편의점에 도착하지 못한 사람이 0명이면 (모두 편의점에 도착하면) 종료
            if (notStorePeople == 0) {
                break;
            }
            // 목표 편의점으로 이동
            toStore();
            for (int i = 1; i < peopleArr.length; i++) {
                // 만약 목표 편의점에 도달했다면, 해당 위치는 이제 불가
                if (peopleArr[i] == null) {
                    continue;
                }
                if (peopleArr[i].x == storeArr[i].x && peopleArr[i].y == storeArr[i].y) {
                    campArr[storeArr[i].x][storeArr[i].y] = -1;
                    notStorePeople--;
                    peopleArr[i] = null;
                }

            }

            // 최단거리 베이스캠프 찾기
            // 시간 > 사람 수 이면 베이스캠프로의 이동 불가
            if (time > m) {
                continue;
            }
            Position position = findBaseCamp();
            peopleArr[time] = new Position(position.x, position.y);

            // 사람 위치 저장
            peopleArr[time] = new Position(position.x, position.y);
            // 해당 위치에 더이상 접근 불가능
            campArr[position.x][position.y] = -1;


        }


        // 3. 출력
        System.out.println(time-1);
    }

    // 격자에 있는 사람들이 본인 목표 편의점 방향으로 1칸 이동
    private static void toStore() {
        for (int i = 1; i < peopleArr.length; i++) {
            if (peopleArr[i] == null) {
                continue;
            }

            // 현재 사람 위치
            Position people = peopleArr[i];

            // 최단거리 계산
            // 현재 사람의 목표 편의점 위치
            Position store = storeArr[i];
            int px = people.x; int py = people.y;
            int sx = store.x;  int sy = store.y;
            int dis = Math.abs(px - sx) + Math.abs(py - sy);

            for (int p = 0; p < 4; p++) {
                int nx = people.x + dx[p];
                int ny = people.y + dy[p];
                int new_dis = Math.abs(nx - sx) + Math.abs(ny - sy);

                // 범위를 벗어나면 이동 불가
                if (nx < 0 || nx >= n || ny < 0 || ny >= n) {
                    continue;
                }

                // 이동 가능한 곳이면
                if (campArr[nx][ny] != -1 && (dis - 1) == new_dis) {
                    peopleArr[i].x = nx;
                    peopleArr[i].y = ny;
                    break;
                }
            }

        }
    }

    private static Position findBaseCamp() {
        int peopleNum = time;   // 이동할 사람 번호 = 현재 시간
        boolean[][] visited = new boolean[n][n];

        int storeX = storeArr[peopleNum].x;
        int storeY = storeArr[peopleNum].y;

        // bfs 실행 - 가장 가까운 베이스캠프 위치 찾기
        Queue<Position> queue = new LinkedList<>();
        queue.add(new Position(storeX, storeY));
        visited[storeX][storeY] = true;

        // 최단거리에서 발견된 베이스캠프 후보 저장 리스트
        ArrayList<Position> baseList = new ArrayList<>();
        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                Position current = queue.poll();

                // 베이스캠프 발견 시 후보 리스트에 저장
                if (campArr[current.x][current.y] == 1) {
                    baseList.add(current);
                }

                for (int p = 0; p < 4; p++) {
                    int nx = current.x + dx[p];
                    int ny = current.y + dy[p];

                    if (nx < 0 || nx >= n || ny < 0 || ny >= n) {
                        continue;
                    }

                    // 이동 가능한 곳이면
                    if (campArr[nx][ny] != -1 && !visited[nx][ny]) {
                        visited[nx][ny] = true;
                        queue.offer(new Position(nx, ny));
                    }
                }
            }
            // 현재 레벨에서 편의점 후보가 발견되면, 정렬하여 반환
            if (!baseList.isEmpty()) {
                // 행, 열 기준으로 우선순위 정렬
                baseList.sort((a,b) -> {
                    if (a.x != b.x) {   // 행 기준 정렬
                        return a.x - b.x;
                    }
                    return a.y - b.y;   // 행이 같으면 열 기준 정렬
                });
                return baseList.get(0); // 최우선 베이스캠프 위치 반환
            }
        }
        return null;
    }


    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        peopleArr = new Position[m + 1];
        campArr = new int[n][n];
        notStorePeople = m;
        storeArr = new Position[m + 1];

        // 베이스캠프 위치 저장
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                campArr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 편의점 위치 저장
        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;

            // 편의점 정보 저장
            storeArr[i] = new Position(x, y);

        }
    }

}