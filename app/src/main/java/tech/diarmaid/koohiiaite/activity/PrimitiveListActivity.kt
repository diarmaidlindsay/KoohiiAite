package tech.diarmaid.koohiiaite.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_primitives_list.*
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.PrimitiveGridAdapter

/**
 * View and rename heisig primitives in a grid view
 */
class PrimitiveListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primitives_list)

        primitive_grid.adapter = PrimitiveGridAdapter(this)
    }
}
