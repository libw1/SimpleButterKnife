package conykais.lib_compiler;

import java.util.List;

public class MethodViewBinding {

    private String name;
    private int[] viewIds;
    private List<Parameter> parameters;

    public MethodViewBinding() {
    }

    public MethodViewBinding(String name, int[] viewIds) {
        this(name,viewIds,null);
    }

    public MethodViewBinding(String name, int[] viewIds, List<Parameter> parameters) {
        this.name = name;
        this.viewIds = viewIds;
        this.parameters = parameters;
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

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
