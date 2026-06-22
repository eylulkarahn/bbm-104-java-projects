import java.util.ArrayList;

public class FindPath {
    private boolean anyPathReachedExit = false;
    private ArrayList<PathElement> bestPath = new ArrayList<>();
    private Room[][] grid;
    private int rows;
    private int cols;

    public FindPath(Room[][] grid, int rows, int cols) {
        this.grid = grid;
        this.rows = rows;
        this.cols = cols;
    }

    public void solve(Player player, int startX, int startY) {
        ArrayList<PathElement> currentPath = new ArrayList<>();

        // start coordinates should be in the path
        currentPath.add(new PathElement(startX, startY, player.getHealth()));

        boolean[][] visited = new boolean[rows][cols];
        visited[startX][startY] = true;

        findPath(player, visited, currentPath);
    }

    private void findPath(Player p, boolean[][] visited, ArrayList<PathElement> currentPath) {
        if (p.isDead()) {
            return;
        }

        int x = p.getX();
        int y = p.getY();

        if (grid[x][y].getType().equals("X")) {
            anyPathReachedExit = true;
        }

        // If reached exit and has key potential best path
        if (p.getReachedExit() && p.getHasKey()) {
            int currentBestHealth = -1;
            int currentBestSteps = Integer.MAX_VALUE;

            // if best path isnt empty, compare health and steps
            if (!bestPath.isEmpty()) {
                currentBestHealth = bestPath.get(bestPath.size() - 1).health;
                currentBestSteps = bestPath.size() - 1;
            }

            int currentHealth = p.getHealth();
            int currentSteps = currentPath.size() - 1;

            // if current path is better than best path, update best path
            if (currentHealth > currentBestHealth) {
                bestPath.clear();
                bestPath.addAll(currentPath);
                // if health is same, compare steps
            } else if (currentHealth == currentBestHealth) {
                if (currentSteps < currentBestSteps) {
                    bestPath.clear();
                    bestPath.addAll(currentPath);
                }
            }
            return;
        }

        // according to the given order
        String[] dirs = { "UP", "RIGHT", "DOWN", "LEFT" };
        for (String dir : dirs) {
            Player tempP = new Player(p.getMaxHealth(), p.getHealth(), p.getShield(), p.getShieldPower(), p.getX(),
                    p.getY(), p.getHasKey(), p.getReachedExit());

            move(tempP, dir);

            int nx = tempP.getX();
            int ny = tempP.getY();

            // if player moved and not already visited
            if ((nx != p.getX() || ny != p.getY()) && !visited[nx][ny]) {
                if (tempP.isDead()) {
                    continue;
                }

                // add to path and visited
                visited[nx][ny] = true;
                currentPath.add(new PathElement(nx, ny, tempP.getHealth()));

                findPath(tempP, visited, currentPath);

                // backtrack
                visited[nx][ny] = false;
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    private void move(Player player, String direction) {
        int nx = player.getX();
        int ny = player.getY();

        if (direction.equals("UP")) {
            nx--;
        } else if (direction.equals("DOWN")) {
            nx++;
        } else if (direction.equals("LEFT")) {
            ny--;
        } else if (direction.equals("RIGHT")) {
            ny++;
        }

        if (nx >= 0 && nx < rows && ny >= 0 && ny < cols) {
            player.setX(nx);
            player.setY(ny);
            grid[nx][ny].applyEffect(player);
        }
    }

    // getters
    public boolean isAnyPathReachedExit() {
        return anyPathReachedExit;
    }

    public ArrayList<PathElement> getBestPath() {
        return bestPath;
    }
}
