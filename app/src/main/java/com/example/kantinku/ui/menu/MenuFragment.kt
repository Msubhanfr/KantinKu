package com.example.kantinku.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kantinku.data.database.KantinDatabase
import com.example.kantinku.data.entity.Menu
import com.example.kantinku.data.repository.MenuRepository
import com.example.kantinku.databinding.FragmentMenuBinding
import com.example.kantinku.ui.cart.CartActivity
import com.example.kantinku.utils.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MenuAdapter
    private lateinit var repository: MenuRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = KantinDatabase.getInstance(requireContext())
        repository = MenuRepository(database.menuDao())

        setupRecyclerView()
        setupTabs()
        loadMenu("Semua")
    }

    private fun setupRecyclerView() {
        adapter = MenuAdapter(
            onItemClick = { menu ->
                // Tampilkan detail menu
                Toast.makeText(requireContext(), "${menu.name}\nRp ${menu.price}", Toast.LENGTH_SHORT).show()
            },
            onOrderClick = { menu ->
                if (menu.stock > 0) {
                    // Kirim ke Cart
                    val intent = Intent(requireContext(), CartActivity::class.java).apply {
                        putExtra(Constants.EXTRA_MENU_ID, menu.id)
                        putExtra(Constants.EXTRA_MENU_NAME, menu.name)
                        putExtra(Constants.EXTRA_MENU_PRICE, menu.price)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Maaf, ${menu.name} sedang habis!", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter
    }

    private fun setupTabs() {
        Constants.CATEGORIES.forEach { category ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category))
        }

        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val category = tab?.text.toString()
                loadMenu(category)
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadMenu(category: String) {
        lifecycleScope.launch {
            val menus = if (category == "Semua") {
                repository.getAllMenu()
            } else {
                repository.getMenuByCategory(category)
            }
            menus.collectLatest { menuList ->
                adapter.submitList(menuList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}