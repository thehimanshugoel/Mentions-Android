# Android Library Code for User mentions in EditText




## Usage
In order to use this module, your Activity or Class should implement MentionDelegate and instantiate MentionController as follows.

```
public class MainActivity extends AppCompatActivity implements MentionDelegate {
    MentionController mentionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mentionController = new MentionController(MainActivity.this, R.id.listView, R.id.edtText, android.R.layout.simple_list_item_1);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getSuggestions(CharSequence text, MentionSuggestionsCallback mentionSuggestionsCallback) {
        List<MentionSuggestible> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add((MentionSuggestible) new MentionSuggestibleImpl(i));
        }
        mentionSuggestionsCallback.onReceived(list);
    }
}
```