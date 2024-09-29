import java.io.*;
import java.util.*;

class Position {
    int x, y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {
    private static int n, m;
    private static Position[] peopleArr;
    private static int[][] campArr;
    private static Position[] storeArr;
    private static int notStorePeople;

    private static int[] dx = {1,0,0,-1};
    private static int[] dy = {0,1,-1,0};
    private static int[][] shortestPath;
    private static boolean[][] visited;

    public static void main(String[] args) throws IOException {
        input();
        simulateGame();
    }

    private static void simulateGame() {
        shortestPath = new int[n][n];
        visited = new boolean[n][n];

        int time = 1;
        while (notStorePeople > 0) {
            for (int i = 1; i <= m; i++) {
                if (peopleArr[i] != null) {
                    bfsFromStore(i);
                    movePerson(i);
                }
            }

            if (time <= m) {
                Position baseCamp = findNearestBaseCamp(storeArr[time]);
                peopleArr[time] = baseCamp;
                campArr[baseCamp.x][baseCamp.y] = -1;
            }
            time++;
        }
        System.out.println(time);
    }

    private static void bfsFromStore(int storeIndex) {
        for (int i = 0; i < n; i++) {
            Arrays.fill(shortestPath[i], Integer.MAX_VALUE);
            Arrays.fill(visited[i], false);
        }

        Queue<Position> queue = new LinkedList<>();
        Position store = storeArr[storeIndex];
        queue.offer(store);
        visited[store.x][store.y] = true;
        shortestPath[store.x][store.y] = 0;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            for (int d = 0; d < 4; d++) {
                int nx = current.x + dx[d], ny = current.y + dy[d];
                if (nx >= 0 && nx < n && ny >= 0 && ny < n && !visited[nx][ny] && campArr[nx][ny] != -1) {
                    visited[nx][ny] = true;
                    queue.offer(new Position(nx, ny));
                    shortestPath[nx][ny] = shortestPath[current.x][current.y] + 1;
                }
            }
        }
    }

    private static void movePerson(int personIndex) {
        Position person = peopleArr[personIndex];
        Position store = storeArr[personIndex];

        if (person.x == store.x && person.y == store.y) {
            campArr[store.x][store.y] = -1;
            notStorePeople--;
            peopleArr[personIndex] = null;
            return;
        }

        int minDistance = shortestPath[person.x][person.y];
        int moveDirection = -1;
        for (int d = 0; d < 4; d++) {
            int nx = person.x + dx[d], ny = person.y + dy[d];
            if (nx >= 0 && nx < n && ny >= 0 && ny < n && campArr[nx][ny] != -1 && shortestPath[nx][ny] < minDistance) {
                minDistance = shortestPath[nx][ny];
                moveDirection = d;
            }
        }

        if (moveDirection != -1) {
            peopleArr[personIndex] = new Position(person.x + dx[moveDirection], person.y + dy[moveDirection]);
        }
    }

    private static Position findNearestBaseCamp(Position store) {
        for (int i = 0; i < n; i++) {
            Arrays.fill(visited[i], false);
        }

        Queue<Position> queue = new LinkedList<>();
        queue.offer(store);
        visited[store.x][store.y] = true;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (campArr[current.x][current.y] == 1) {
                return current;
            }

            for (int d = 0; d < 4; d++) {
                int nx = current.x + dx[d], ny = current.y + dy[d];
                if (nx >= 0 && nx < n && ny >= 0 && ny < n && !visited[nx][ny] && campArr[nx][ny] != -1) {
                    visited[nx][ny] = true;
                    queue.offer(new Position(nx, ny));
                }
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

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                campArr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            storeArr[i] = new Position(x, y);
        }
    }
}