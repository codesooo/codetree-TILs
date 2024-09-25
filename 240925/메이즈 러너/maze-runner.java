import java.io.*;
import java.util.*;

class Pair{
    int x;
    int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {
    private static final int MAX_N = 10;
    private static int N, M, K; // 미로 크리, 참가자 수, 게임 시간
    private static int[][] wallMap = new int[MAX_N][MAX_N]; // 모든 벽들의 상태
    private static int[][] nextBoard = new int[MAX_N][MAX_N];   // 회전 구현을 위한 배열
    private static LinkedList<Pair> travelerList = new LinkedList<>();    // 참가자 위치 정보
    private static Pair exit;   // 출구 위치 정보
    private static int ans;     // 모든 참가자들의 이동 거리의 합
    private static int sx, sy, squareSize;  // 최소 정사각형

    public static void main(String[] args) throws IOException {

        // 0. 입력받기
        input();


        while (K-- > 0) {
            // 모든 참가자를 이동시킴
            moveAllTraveler();

            // 모든 참가자가 탈출했는지 여부 확인
            if (checkAllTravelerEscape()) {
                break;
            }

            // 가장 작은 정사각형 찾기 (한 명 이상의 참가자와 출구를 포함)
            findMinimumSquare();

            // 정사각형 벽 회전
            rotateSquare();

            // 정사각형 내 참가자, 출구 회전
            rotateTravelerAndExit();

        }
        System.out.println(ans);
        System.out.println((exit.x + 1) + " " + (exit.y + 1));
    }

    private static void rotateSquare() {
        // 벽 내구도 1 감소
        for (int x = sx; x < sx + squareSize; x++) {
            for (int y = sy; y < sy + squareSize; y++) {
                if (wallMap[x][y] > 0) {
                    wallMap[x][y] -= 1;
                }
            }
        }

        // 시계방향으로 90도 회전
        for (int x = sx; x < sx + squareSize; x++) {
            for (int y = sy; y < sy + squareSize; y++) {

                // (0,0)부터 시작 (상대좌표)
                int ox = x - sx;
                int oy = y - sy;

                // 회전된 좌표
                int rx = oy;
                int ry = squareSize - ox -1;

                nextBoard[rx + sx][ry + sy] = wallMap[x][y];


            }
        }
        // 원본 배열에 옮기기
        for (int x = sx; x < sx + squareSize; x++) {
            for (int y = sy; y < sy + squareSize; y++) {
                wallMap[x][y] = nextBoard[x][y];
            }
        }
    }

    private static void rotateTravelerAndExit() {
        for (Pair traveler : travelerList) {
            // 정사각형 안에 있는 참가자이면 회전
            if (sx <= traveler.x && traveler.x < sx + squareSize && sy <= traveler.y && traveler.y < sy + squareSize) {
                int ox = traveler.x - sx;
                int oy = traveler.y - sy;

                int rx = oy;
                int ry = squareSize - ox -1;

                traveler.x = rx + sx;
                traveler.y = ry + sy;
            }
        }

        if (sx <= exit.x && exit.x < sx + squareSize && sy <= exit.y && exit.y < sy  + squareSize) {
            int ox = exit.x - sx;
            int oy = exit.y - sy;

            int rx = oy;
            int ry = squareSize - ox -1;

            exit.x = rx + sx;
            exit.y = ry + sy;
        }
    }


    // 한명 이상의 참가자와 출구를 포함한 가장 작은 정사각형 찾기
    private static void findMinimumSquare() {
        for (int sz = 2; sz <= N; sz++) {
            // 가장 작은 정사각형부터 모든 정사각형을 만들기
            for (int x1 = 0; x1 < N - sz + 1; x1++) {

                // 가장 좌상단 r좌표가 작은 것부터 하나씩 만들기
                for (int y1 = 0; y1 < N - sz + 1; y1++) {

                    int x2 = x1 + sz -1;
                    int y2 = y1 + sz -1;
                    // 출구가 해당 정사각형 안에 없으면 스킵
                    if (!(x1 <= exit.x && exit.x <= x2 && y1 <= exit.y && exit.y <= y2)) {
                        continue;
                    }

                    // 한명 이상의 참가자가 해당 정사각형 안에 있다면
                    // -> sx, sy, squareSize 정보 갱신 후 종료
                    if ((isTravelerInSquare(x1, x2, y1, y2))) {
                        sx = x1;
                        sy = y1;
                        squareSize = sz;

                        return;
                    }


                }
            }
        }
    }

    // 한명 이상의 참가자가 해당 정사각형 안에 있는지 판단
    private static boolean isTravelerInSquare(int x1, int x2, int y1, int y2) {
        for (Pair traveler : travelerList) {
            if (x1 <= traveler.x && traveler.x <= x2
                    && y1 <= traveler.y && traveler.y <= y2) {
                // 출구에 있는 참가자는 제외
                if (!(traveler.x == exit.x && traveler.y == exit.y)) {
                    return true;
                }
            }
        }

        // 없으면 false 반환
        return false;
    }

    private static boolean checkAllTravelerEscape() {
        for (Pair traveler : travelerList) {
            // 출구가 아닌 곳에 위치한 참가자가 있으면
            if (!(traveler.x == exit.x && traveler.y == exit.y)) {
                return false;
            }
        }
        return true;
    }
    // 모든 참가자를 이동시킴
    private static void moveAllTraveler() {
        // m명의 모든 참가자들에 대해 이동 진행
        for (Pair traveler : travelerList) {
            // 이미 출구에 있는 경우 스킵
            if (traveler.x == exit.x && traveler.y == exit.y) {
                continue;
            }

            // 참가자와 출구의 행이 다른 경우, 행을 이동시킴 (직선 세로선상 or 대각선상 위치)
            if (traveler.x != exit.x) {
                int nx = traveler.x;
                int ny = traveler.y;

                // 출구와 가까운 곳으로 이동
                if (exit.x > nx) {
                    nx += 1;
                } else {
                    nx -= 1;
                }
                // 벽이 없으면 행 이동 가능 -> 이동 후 다음 참가자로 넘어감
                if (wallMap[nx][ny] == 0) {
                    ans += 1;  // 이동 횟수 증가
                    traveler.x = nx;
                    traveler.y = ny;
                    continue;
                }
            }

            // 참가자와 출구의 열이 다른 경우 열을 이동시킴  (직산 가로선상 위치)
            if (traveler.y != exit.y) {
                int nx = traveler.x;
                int ny = traveler.y;

                // 출구와 가까운 곳으로 이동
                if (exit.y > ny) {
                    ny += 1;
                } else {
                    ny -= 1;
                }
                // 벽이 없으면 열 이동 가능
                if (wallMap[nx][ny] == 0) {
                    ans += 1;  // 이동 횟수 증가
                    traveler.x = nx;
                    traveler.y = ny;
                }
            }
        }
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                wallMap[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            travelerList.add(new Pair(x, y));
        }

        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken()) - 1;
        int y = Integer.parseInt(st.nextToken()) - 1;
        exit = new Pair(x, y);
    }


}