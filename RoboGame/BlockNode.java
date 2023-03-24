public interface BlockNode extends ProgramNode {
  void preExec(Robot robot);
  void postExec(Robot robot);

  BlockNode NULL = new BlockNode() {
    public void preExec(Robot robot) { }

    public void postExec(Robot robot) { }

    public void execute(Robot robot) { }

    public String toString() {
      return "NULL";
    }
  };
}