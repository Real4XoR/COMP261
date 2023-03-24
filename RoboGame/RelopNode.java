public interface RelopNode {
  boolean execute(int a, int b);

  RelopNode LT = new RelopNode() {
    public boolean execute(int a, int b) {
      return a < b;
    }

    public String toString() {
      return "lt";
    }
  };

  RelopNode GT = new RelopNode() {
    public boolean execute(int a, int b) {
      return a > b;
    }

    public String toString() {
      return "gt";
    }
  };

  RelopNode EQ = new RelopNode() {
    public boolean execute(int a, int b) {
      return a == b;
    }

    public String toString() {
      return "eq";
    }
  };
}