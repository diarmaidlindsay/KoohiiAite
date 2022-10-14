package tech.diarmaid.koohiiaite.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.diarmaid.koohiiaite.adapter.PrimitiveGridAdapter
import tech.diarmaid.koohiiaite.databinding.ActivityPrimitivesListBinding

/**
 * View and rename heisig primitives in a grid view
 */
class PrimitiveListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrimitivesListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrimitivesListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.primitiveGrid.adapter = PrimitiveGridAdapter(this)
    }
}
