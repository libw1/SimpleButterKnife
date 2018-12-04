package conykais.lib_compiler;

public class MethodViewBinding {

    private String name;
    private int[] viewIds;

    public MethodViewBinding() {
    }

    public MethodViewBinding(String name, int[] viewIds) {
        this.name = name;
        this.viewIds = viewIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getViewIds() {
        return viewIds;
    }

    public void setViewIds(int[] viewIds) {
        this.viewIds = viewIds;
    }
}
