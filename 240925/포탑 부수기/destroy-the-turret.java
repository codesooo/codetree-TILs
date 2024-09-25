import java.io.*;
import java.util.*;

class Position {
    int x;
    int y;
    int distance;
    Position parent;
    int attackTime;

    public Position(int x, int y, int distance, Position parent) {
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.parent = parent;
    }

    public Position(int x, int y, int attackTime) {
        this.x = x;
        this.y = y;
        this.attackTime = attackTime;
    }

}

public class Main {
    private static int N, M, K;
    private static int[][] topMap;
    // 우 / 하 / 좌 / 상
    private static int[] dx4 = {0, 1, 0, -1};
    private static int[] dy4 = {1, 0, -1, 0};

    private static int[] dx8 = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static int[] dy8 = {-1, 0, 1, -1, 1, -1, 0, 1};

    private static int[][] previousMap;

    private static PriorityQueue<Position> attackerPrior = new PriorityQueue<>(
            Comparator.comparingInt((Position p) -> topMap[p.x][p.y])
                    .thenComparingInt(p -> -p.attackTime)
                    .thenComparingInt(p -> -(p.x + p.y))
                    .thenComparingInt(p -> -p.y)
    );;
    private static PriorityQueue<Position> targetPrior = new PriorityQueue<>(
            Comparator.comparingInt((Position p) -> -topMap[p.x][p.y])
                    .thenComparingInt(p -> p.attackTime)
                    .thenComparingInt(p -> p.x + p.y)
                    .thenComparingInt(p -> p.y)
    );
    public static void main(String[] args) throws IOException{
        // 0. 입력받기
        input();

        // 1. 구현
        for (int k = 0; k < K; k++) {
            // <종료 조건>
            // 포탑의 개수가 1개이면 종료
            if (attackerPrior.size() <= 1) {
                break;
            }


            // # 공격 전의 배열 저장
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    previousMap[i][j] = topMap[i][j];

                }
            }

            // 1) 공격자 선정
            // 공격자 위치 저장
            Position attackPosition = attackerPrior.peek();

            // 공격자 공격력 +(N+M)
            topMap[attackPosition.x][attackPosition.y] += (N + M);

            // 공격시점 저장
            attackPosition.attackTime = k + 1;

            int[] attacker = {attackPosition.x, attackPosition.y};

            // 공격 대상 큐에도 공격 시점 저장
            for (Position position : targetPrior) {
                if (position.x == attackPosition.x && position.y == attackPosition.y) {
                    position.attackTime = k + 1;
                }
            }

            // 2) 공격 대상 선정
            // 공격 대상 우선순위 큐에서 추출
            Position targetPosition = targetPrior.peek();
            // 공격 대상 위치 저장
            int[] target = {targetPosition.x, targetPosition.y};

            // 3-1) 레이저 공격
            if (!laser(attacker, target)) {

                // 3-2) 포탄 공격
                potan(attacker, target);
            }

            // 4) 포탑 정비

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    // 공격력이 줄어들지 않았고, 공격자가 아닐 때
                    if (topMap[i][j] > 0
                            && previousMap[i][j] == topMap[i][j]
                            && !(i == attacker[0] && j == attacker[1])) {
                        topMap[i][j] += 1;
                    }
                }
            }

            // 부서진 포탑 제거
            attackerPrior.removeIf(item -> topMap[item.x][item.y] <= 0);
            targetPrior.removeIf(item -> topMap[item.x][item.y] <= 0);


        }

        // 2. 출력
        int ans = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (topMap[i][j] > 0) {
                    ans = Math.max(ans, topMap[i][j]);
                }
            }
        }
        System.out.println(ans);

    }


    private static boolean laser(int[] attacker, int[] target) {
        int ax = attacker[0];   int ay = attacker[1];
        int tx = target[0];     int ty = target[1];

        Queue<Position> queue = new LinkedList<>();
        boolean[][] visited = new boolean[N][M];
        visited[ax][ay] = true;
        queue.add(new Position(ax,ay,0,null));
        while (!queue.isEmpty()) {
            Position current = queue.poll();

            // 공격 대상에 도착하면
            if (current.x == tx && current.y == ty) {
                // 공격 대상의 공격력 업데이트
                topMap[tx][ty] -= topMap[ax][ay];

                // 레이저 경로에 있는 (지나온 모든 경로)의 공격력 업데이트
                List<Position> path = new ArrayList<>();

                Position now = current.parent;

                // parent가 공격자가 되기 전까지
                while (!(now.x == ax && now.y == ay)) {
                    path.add(now);
                    now = now.parent;   // 현재 Position을 parent에 저장된 객체로 변환
                }

                for (Position position : path) {
                    topMap[position.x][position.y] -= (topMap[ax][ay] / 2);
                }

                // 레이저 공격이 가능한 경우이므로, true 반환
                return true;

            }

            for (int p = 0; p < 4; p++) {
                int nx = current.x + dx4[p];
                int ny = current.y + dy4[p];

                // 좌표가 범위를 벗어나는 경우 -> 벽 뚫고 이동
                if (nx >= N ) {
                    nx -= N;
                } else if (nx < 0) {
                    nx += N;
                }
                if (ny >= M) {
                    ny -= M;
                } else if (ny < 0) {
                    ny += M;
                }

                // 부서진 탑이 있는 곳 or 이미 방문한 곳 -> 가지 않음
                if (topMap[nx][ny] <= 0 || visited[nx][ny]) {
                    continue;
                }

                // 이동 가능한 경우
                Position newPosition = new Position(nx, ny, current.distance + 1, current);
                queue.add(newPosition);
                visited[nx][ny] = true;

            }
        }

        return false;
    }

    private static void potan(int[] attacker, int[] target) {
        int ax = attacker[0];   int ay = attacker[1];
        int tx = target[0];     int ty = target[1];

        // 공격대상의 공격력 업데이트
        topMap[tx][ty] -= topMap[ax][ay];

        for (int p = 0; p < 8; p++) {
            int nx = tx + dx8[p];
            int ny = ty + dy8[p];

            // 좌표가 범위를 벗어나는 경우 -> 벽 뚫고 이동
            if (nx >= N ) {
                nx -= N;
            } else if (nx < 0) {
                nx += N;
            }
            if (ny >= M) {
                ny -= M;
            } else if (ny < 0) {
                ny += M;
            }

            // 탑이 부서진 곳이 아니고, 공격자의 위치가 아니면 공격력 업데이트
            if (topMap[nx][ny] > 0 && !(nx == ax && ny == ay)) {
                topMap[nx][ny] -= (topMap[ax][ay] / 2);
            }
        }
    }


    private static void input() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        topMap = new int[N][M];
        previousMap = new int[N][M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                topMap[i][j] = Integer.parseInt(st.nextToken());
                if (topMap[i][j] > 0) {
                    attackerPrior.add(new Position(i, j, 0));
                    targetPrior.add(new Position(i, j, 0));
                }
            }
        }
    }
}