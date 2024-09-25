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
    private static int[] dx4 = {0, 1, 0, -1};
    private static int[] dy4 = {1, 0, -1, 0};
    private static int[] dx8 = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static int[] dy8 = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static int[][] previousMap;

    private static Comparator<Position> attackerComparator = (p1, p2) -> {
        if (topMap[p1.x][p1.y] != topMap[p2.x][p2.y])
            return Integer.compare(topMap[p1.x][p1.y], topMap[p2.x][p2.y]);
        if (p1.attackTime != p2.attackTime)
            return Integer.compare(-p1.attackTime, -p2.attackTime);
        if (p1.x + p1.y != p2.x + p2.y)
            return Integer.compare(-(p1.x + p1.y), -(p2.x + p2.y));
        return Integer.compare(-p1.y, -p2.y);
    };

    private static Comparator<Position> targetComparator = (p1, p2) -> {
        if (topMap[p1.x][p1.y] != topMap[p2.x][p2.y])
            return Integer.compare(-topMap[p1.x][p1.y], -topMap[p2.x][p2.y]);
        if (p1.attackTime != p2.attackTime)
            return Integer.compare(p1.attackTime, p2.attackTime);
        if (p1.x + p1.y != p2.x + p2.y)
            return Integer.compare(p1.x + p1.y, p2.x + p2.y);
        return Integer.compare(p1.y, p2.y);
    };

    private static PriorityQueue<Position> attackerPrior = new PriorityQueue<>(attackerComparator);
    private static PriorityQueue<Position> targetPrior = new PriorityQueue<>(targetComparator);

    public static void main(String[] args) throws IOException {
        input();

        for (int k = 0; k < K; k++) {
            if (attackerPrior.size() <= 1) {
                break;
            }

            for (int i = 0; i < N; i++) {
                System.arraycopy(topMap[i], 0, previousMap[i], 0, M);
            }

            Position attackPosition = attackerPrior.peek();
            topMap[attackPosition.x][attackPosition.y] += (N + M);
            attackPosition.attackTime = k + 1;
            int[] attacker = {attackPosition.x, attackPosition.y};

            for (Position position : targetPrior) {
                if (position.x == attackPosition.x && position.y == attackPosition.y) {
                    position.attackTime = k + 1;
                    break;
                }
            }

            Position targetPosition = targetPrior.peek();
            int[] target = {targetPosition.x, targetPosition.y};

            if (!laser(attacker, target)) {
                potan(attacker, target);
            }

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (topMap[i][j] > 0
                            && previousMap[i][j] == topMap[i][j]
                            && !(i == attacker[0] && j == attacker[1])) {
                        topMap[i][j] += 1;
                    }
                }
            }

            attackerPrior.removeIf(item -> topMap[item.x][item.y] <= 0);
            targetPrior.removeIf(item -> topMap[item.x][item.y] <= 0);
        }

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
        int ax = attacker[0];
        int ay = attacker[1];
        int tx = target[0];
        int ty = target[1];

        Queue<Position> queue = new LinkedList<>();
        boolean[][] visited = new boolean[N][M];
        visited[ax][ay] = true;
        queue.add(new Position(ax, ay, 0, null));
        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.x == tx && current.y == ty) {
                topMap[tx][ty] -= topMap[ax][ay];

                List<Position> path = new ArrayList<>();
                Position now = current.parent;

                while (!(now.x == ax && now.y == ay)) {
                    path.add(now);
                    now = now.parent;
                }

                for (Position position : path) {
                    topMap[position.x][position.y] -= (topMap[ax][ay] / 2);
                }

                return true;
            }

            for (int p = 0; p < 4; p++) {
                int nx = current.x + dx4[p];
                int ny = current.y + dy4[p];

                if (nx >= N) {
                    nx -= N;
                } else if (nx < 0) {
                    nx += N;
                }
                if (ny >= M) {
                    ny -= M;
                } else if (ny < 0) {
                    ny += M;
                }

                if (topMap[nx][ny] <= 0 || visited[nx][ny]) {
                    continue;
                }

                Position newPosition = new Position(nx, ny, current.distance + 1, current);
                queue.add(newPosition);
                visited[nx][ny] = true;
            }
        }

        return false;
    }

    private static void potan(int[] attacker, int[] target) {
        int ax = attacker[0];
        int ay = attacker[1];
        int tx = target[0];
        int ty = target[1];

        topMap[tx][ty] -= topMap[ax][ay];

        for (int p = 0; p < 8; p++) {
            int nx = tx + dx8[p];
            int ny = ty + dy8[p];

            if (nx >= N) {
                nx -= N;
            } else if (nx < 0) {
                nx += N;
            }
            if (ny >= M) {
                ny -= M;
            } else if (ny < 0) {
                ny += M;
            }

            if (topMap[nx][ny] > 0 && !(nx == ax && ny == ay)) {
                topMap[nx][ny] -= (topMap[ax][ay] / 2);
            }
        }
    }

    private static void input() throws IOException {
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