package conykais.bindview2;

import android.view.View;

public abstract class BindViewOnClickListener implements View.OnClickListener{

    @Override
    public void onClick(View v) {
        doWord(v);
    }

    public abstract void doWord(View v);
}
