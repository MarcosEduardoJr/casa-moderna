package com.casamoderna.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.casamoderna.R
import com.casamoderna.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sign.*

internal const val RC_SIGN_IN = 1337

class SignFragment : Fragment() {
    private var user: User? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sign, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()
        google.setOnClickListener {
            // Check if user is signed in (non-null) and update UI accordingly.
            updateUI()
        }
    }

    private fun updateUI() {
        progress.visibility = View.VISIBLE
        if (user == null) startSignInflow() else nextPage()
    }

    private fun nextPage() {
        progress.visibility = View.INVISIBLE
        val action =
            SignFragmentDirections.nextCategoryProfile(
                currentUser = user!!
            )
        Navigation.findNavController(v).navigate(action)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity?.let {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        saveUser(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Snackbar.make(v, "Salvo com sucesso", Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun saveUser(user: FirebaseUser?) {
        this.user = User(
            uuid = user?.uid,
            name = user?.displayName,
            phoneNumber = user?.phoneNumber,
            email = user?.email
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .add(this.user!!)
            .addOnSuccessListener {
                updateUI()
            }
            .addOnFailureListener {
                Snackbar.make(
                    v,
                    "Não foi possivel salvar, tente novamente mais tarde",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    private fun startSignInflow() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            }
        } catch (exception: Exception) {
            Log.d("LOGIN", exception.toString())
        }

    }

}
