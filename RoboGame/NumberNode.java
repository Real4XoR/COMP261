public interface NumberNode {
  int execute(Robot robot);

  NumberNode FUEL_LEFT = new NumberNode() {
    public int execute(Robot robot) {
      return robot.getFuel();
    }

    public String toString() {
      return "fuelLeft";
    }
  };

  NumberNode OPP_LR = new NumberNode() {
    public int execute(Robot robot) {
      return robot.getOpponentLR();
    }

    public String toString() {
      return "oppLR";
    }
  };

  NumberNode OPP_FB = new NumberNode() {
    public int execute(Robot robot) {
      return robot.getOpponentFB();
    }

    public String toString() {
      return "oppFB";
    }
  };

  NumberNode NUM_BARRELS = new NumberNode() {
    public int execute(Robot robot) {
      return robot.numBarrels();
    }

    public String toString() {
      return "numBarrels";
    }
  };

  NumberNode BARREL_LR = new NumberNode() {
    public int execute(Robot robot) {
      return robot.getClosestBarrelLR();
    }

    public String toString() {
      return "barrelLR";
    }
  };

  NumberNode BARREL_FB = new NumberNode() {
    public int execute(Robot robot) {
      return robot.getClosestBarrelFB();
    }

    public String toString() {
      return "barrelFB";
    }
  };

  NumberNode WALL_DIST = new NumberNode() {
    public int execute(Robot robot) {
      return robot.getDistanceToWall();
    }

    public String toString() {
      return "wallDist";
    }
  };
}
