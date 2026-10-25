package com.example.flowcomparison

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigInteger

class CalculationViewModel : ViewModel() {


//    Cold flow
     val fib: Flow<BigInteger> = flow {
        TODO("Implement Fibonacci Cold flow")
    }

//    Hot flow
//    val _fib = MutableStateFlow(BigInteger.ONE)
//    val fib = _fib.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            TODO("Implement Fibonacci HOT flow")
//        }
//    }


}