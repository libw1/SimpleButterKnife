package conykais.lib_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import conykais.lib_annotation.BindView;
import conykais.lib_annotation.OnClick;

public class BindViewProcessor extends AbstractProcessor{

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        set.add(OnClick.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<FieldViewBinding>> targetMap = getTargetMap(roundEnvironment);
        Map<TypeElement, List<MethodViewBinding>> methodMap = getMethodMap(roundEnvironment);

        createJavaFile(targetMap.entrySet(), methodMap);
        return false;
    }


    private void createJavaFile(Set<Map.Entry<TypeElement, List<FieldViewBinding>>> entries,
                                Map<TypeElement, List<MethodViewBinding>> map) {
        for (Map.Entry<TypeElement, List<FieldViewBinding>> entry : entries){
            TypeElement typeElement = entry.getKey();
            List<FieldViewBinding> list = entry.getValue();
            if (list == null || list.size() == 0){
                continue;
            }

            String pkgName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            String className = typeElement.getQualifiedName().toString().substring(pkgName.length() + 1);
            String newClassName = className + "_ViewBinding";

            MethodSpec.Builder methodSpec = MethodSpec.constructorBuilder()
                    .addParameter(ClassName.bestGuess(className),"target",Modifier.FINAL)
                    .addModifiers(Modifier.PUBLIC);

            for (FieldViewBinding fieldViewBinding : list){
                String viewClassName = fieldViewBinding.getTypeName().toString();
                ClassName viewClass = ClassName.bestGuess(viewClassName);
                methodSpec.addStatement("target.$L = ($T)target.findViewById($L)",fieldViewBinding.getFieldName(),viewClass,fieldViewBinding.getViewId());
            }

            List<MethodViewBinding> methodViewBindings = map.get(typeElement);
            if (methodViewBindings != null && methodViewBindings.size() > 0) {
                for (MethodViewBinding methodViewBinding : methodViewBindings) {
                    for (int i : methodViewBinding.getViewIds()) {
                        MethodSpec.Builder addMethodBuilder = MethodSpec.methodBuilder("doWord")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.bestGuess("android.view.View"), "v");
                        List<Parameter> parameters = methodViewBinding.getParameters();
                        if (parameters == null || parameters.size() == 0){
                            addMethodBuilder.addStatement("target.$L()",methodViewBinding.getName());
                        }else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("target.$L(");
                            for(int j = 0; j < parameters.size(); j ++){
                                TypeName typeName = parameters.get(j).getTypeName();
                                if (typeName.isBoxedPrimitive()){
                                    if (typeName.equals(TypeName.BOOLEAN)){
                                        sb.append("false");
                                    } else if (typeName.equals(TypeName.BYTE)){
                                        sb.append("0");
                                    }else if (typeName.equals(TypeName.CHAR)){
                                        sb.append("a");
                                    }else if (typeName.equals(TypeName.DOUBLE)){
                                        sb.append("0");
                                    }else if (typeName.equals(TypeName.FLOAT)){
                                        sb.append("0");
                                    }else if (typeName.equals(TypeName.INT)){
                                        sb.append("0");
                                    }else if (typeName.equals(TypeName.LONG)){
                                        sb.append("0L");
                                    }else if (typeName.equals(TypeName.SHORT)){
                                        sb.append("0");
                                    }
                                }else if (typeName.toString().equalsIgnoreCase("View")){
                                    sb.append("v");
                                }else {
                                    sb.append("null");
                                }
                                if (j != parameters.size() - 1){
                                    sb.append(",");
                                } else {
                                    sb.append(")");
                                }
                            }
                            addMethodBuilder.addStatement(sb.toString(),methodViewBinding.getName());
                        }

                        TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                                .superclass(ClassName.bestGuess("conykais.bindview2.BindViewOnClickListener"))
                                .addMethod(addMethodBuilder.build())
                                .build();

                        methodSpec.addStatement("target.findViewById($L).setOnClickListener($L)", i, typeSpec);
                    }
                }
                map.remove(typeElement);
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(newClassName)
                    .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                    .addMethod(methodSpec.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(pkgName, typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (map.size() <= 0){
            return;
        }
        for (Map.Entry<TypeElement, List<MethodViewBinding>> listEntry : map.entrySet()){
            TypeElement element = listEntry.getKey();
            List<MethodViewBinding> methodViewBindingList = listEntry.getValue();
            if (methodViewBindingList == null || methodViewBindingList.size() == 0){
                continue;
            }

            String pkg = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String clazzName = element.getQualifiedName().toString().substring(pkg.length() + 1);
            String newClazzName = clazzName + "_ViewBinding";

            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(clazzName),"target",Modifier.FINAL);

            for (MethodViewBinding methodViewBinding : methodViewBindingList) {
                for (int i : methodViewBinding.getViewIds()) {
                    MethodSpec.Builder addMethodBuilder = MethodSpec.methodBuilder("doWord")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ClassName.bestGuess("android.view.View"), "v");
                    List<Parameter> parameters = methodViewBinding.getParameters();
                    if (parameters == null || parameters.size() == 0){
                        addMethodBuilder.addStatement("target.$L()",methodViewBinding.getName());
                    }else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("target.$L(");
                        for(int j = 0; j < parameters.size(); j ++){
                            TypeName typeName = parameters.get(j).getTypeName();
                            if (typeName.isBoxedPrimitive()){
                                if (typeName.equals(TypeName.BOOLEAN)){
                                    sb.append("false");
                                } else if (typeName.equals(TypeName.BYTE)){
                                    sb.append("0");
                                }else if (typeName.equals(TypeName.CHAR)){
                                    sb.append("a");
                                }else if (typeName.equals(TypeName.DOUBLE)){
                                    sb.append("0");
                                }else if (typeName.equals(TypeName.FLOAT)){
                                    sb.append("0");
                                }else if (typeName.equals(TypeName.INT)){
                                    sb.append("0");
                                }else if (typeName.equals(TypeName.LONG)){
                                    sb.append("0L");
                                }else if (typeName.equals(TypeName.SHORT)){
                                    sb.append("0");
                                }
                            }else if (typeName.toString().equalsIgnoreCase("View")){
                                sb.append("v");
                            }else {
                                sb.append("null");
                            }
                            if (j != parameters.size() - 1){
                                sb.append(",");
                            } else {
                                sb.append(")");
                            }
                        }
                        addMethodBuilder.addStatement(sb.toString(),methodViewBinding.getName());
                    }

                    TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                            .superclass(ClassName.bestGuess("conykais.bindview2.BindViewOnClickListener"))
                            .addMethod(addMethodBuilder.build())
                            .build();

                    methodBuilder.addStatement("target.findViewById($L).setOnClickListener($L)", i, typeSpec);
                }
            }
//            for (MethodViewBinding methodViewBinding : methodViewBindingList){
//                for (int i : methodViewBinding.getViewIds()) {
//                    TypeSpec typeSpec1 = TypeSpec.anonymousClassBuilder("")
//                            .superclass(ClassName.bestGuess("conykais.bindview2.BindViewOnClickListener"))
//                            .addMethod(MethodSpec.methodBuilder("doWord")
//                                    .addAnnotation(Override.class)
//                                    .addModifiers(Modifier.PUBLIC)
//                                    .addParameter(ClassName.bestGuess("android.view.View"), "v")
//                                    .addStatement("target.$L(v)",methodViewBinding.getName())
//                                    .build()).build();
//                    methodBuilder.addStatement("target.findViewById($L).setOnClickListener($L)", i, typeSpec1);
//                }
//            }

            TypeSpec typeSpec1 = TypeSpec.classBuilder(newClazzName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile1 = JavaFile.builder(pkg,typeSpec1).build();

            try {
                javaFile1.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<TypeElement, List<MethodViewBinding>> getMethodMap(RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<MethodViewBinding>> map = new HashMap<>();

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        for (Element element : elementsAnnotatedWith){
            MethodViewBinding methodViewBinding = new MethodViewBinding();
            String name = element.getSimpleName().toString();
            int[] viewIds = element.getAnnotation(OnClick.class).value();
            List<? extends TypeParameterElement> typeParameters = ((ExecutableElement) element).getTypeParameters();
            if (!typeParameters.isEmpty()){
                List<Parameter> parameters = new LinkedList<>();
                for (int i = 0; i < typeParameters.size() ; i ++){
                    TypeMirror typeMirror = typeParameters.get(i).asType();
                    parameters.add(new Parameter(i, TypeName.get(typeMirror)));
                }
                methodViewBinding.setParameters(parameters);
            }
            TypeElement typeTlement = (TypeElement) element.getEnclosingElement();

            List<MethodViewBinding> list = map.get(typeTlement);
            if (list == null){
                list = new ArrayList<>();
                map.put(typeTlement, list);
            }
            methodViewBinding.setName(name);
            methodViewBinding.setViewIds(viewIds);
            list.add(methodViewBinding);

        }
        return map;
    }

    private Map<TypeElement, List<FieldViewBinding>> getTargetMap(RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();

        Set<? extends Element> bindViewElement = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : bindViewElement){
            TypeMirror typeMirror = element.asType();
            String name = element.getSimpleName().toString();
            int viewId = element.getAnnotation(BindView.class).value();

            TypeElement typeTlement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list = targetMap.get(typeTlement);
            if (list == null){
                list = new ArrayList<>();
                targetMap.put(typeTlement, list);
            }

            list.add(new FieldViewBinding(name,typeMirror,viewId));
        }
        return targetMap;
    }
}
