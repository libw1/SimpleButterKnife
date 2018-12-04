package conykais.bindview2;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;

public class BindViewKnife {

    public static void bind(Activity target){

        String className = target.getClass().getName();

        try {
            Class<?> clazz = Class.forName(className + "_ViewBinding");
            clazz.getConstructor(target.getClass()).newInstance(target);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
