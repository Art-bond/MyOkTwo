package ru.d3st.myoktwo.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import org.json.JSONException
import ru.d3st.myoktwo.BuildConfig.*
import ru.d3st.myoktwo.R
import ru.d3st.myoktwo.databinding.LoginFragmentBinding
import ru.d3st.myoktwo.network.OkMyApi
import ru.ok.android.sdk.*
import ru.ok.android.sdk.util.OkAuthType
import ru.ok.android.sdk.util.OkScope
import java.util.*


class Login : Fragment() {


    private var _bind: LoginFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val bind get() = _bind!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _bind = LoginFragmentBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        //следим за жизненым циклом фрагмента
        val viewModel: LoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        bind.loginData = viewModel
        bind.lifecycleOwner = this

        //подключаем viewmodel
        bind.loginData

        bind.sdkLoginAny.setOnClickListener {

            activity?.let { One ->
                OkMyApi.ok.requestAuthorization(
                    One,
                    REDIRECT_URL, OkAuthType.ANY, OkScope.VALUABLE_ACCESS
                )
            }
            val intent = Intent(activity, OkAuthActivity::class.java)
            intent.putExtra(PARAM_CLIENT_ID, APP_ID)
            intent.putExtra(PARAM_APP_KEY, APP_KEY)
            intent.putExtra(PARAM_REDIRECT_URI, REDIRECT_URL)
            intent.putExtra(PARAM_AUTH_TYPE, OkAuthType.ANY)
            intent.putExtra(PARAM_SCOPES, OkScope.VALUABLE_ACCESS)
            startActivityForResult(intent, OK_AUTH_REQUEST_CODE)
        }
// NOTE: application should use just one of the login methods, ANY is preferable
        bind.sdkLoginSso.setOnClickListener {
            OkMyApi.ok.requestAuthorization(
                requireActivity(),
                REDIRECT_URL,
                OkAuthType.NATIVE_SSO,
                OkScope.VALUABLE_ACCESS
            )
        }
        bind.sdkLoginOauth.setOnClickListener {
            OkMyApi.ok.requestAuthorization(
                requireActivity(),
                REDIRECT_URL,
                OkAuthType.WEBVIEW_OAUTH,
                OkScope.VALUABLE_ACCESS
            )
        }

        bind.sdkGetCurrentuser.setOnClickListener {
            OkMyApi.ok.requestAsync("users.getCurrentUser",
                listener = ContextOkListener(this.requireContext(),
                    onSuccess = { _, json -> toast("Get current user result: $json") },
                    onError = { _, err -> toast("Get current user failed: $err") }
                ))
        }
        bind.sdkGetFriends.setOnClickListener {
            OkMyApi.ok.requestAsync("friends.get",
                listener = ContextOkListener(this.requireContext(),
                    onSuccess = { _, json -> toast("Get user friends result: $json") },
                    onError = { _, err -> toast("Failed to get friends: $err") }
                ))
        }
        bind.sdkLogout.setOnClickListener {
            OkMyApi.ok.clearTokens()
            showAppData(false)
        }

        bind.sdkPost.setOnClickListener {
            val json = """
                {"media": [
                    {"type":"text", "text":"Hello world!"},
                    {"type":"link", "url":"https://apiok.ru/"},
                    {"type":"app",
                        "text":"Welcome from sample",
                        "images":[{"url":"https://apiok.ru/res/img/main/app_create.png"}],
                        "actions":[{"text": "Play me!", "mark": "play_me_from_app_block"}]
                    }]
                }
            """.trimIndent()
            OkMyApi.ok.performPosting(this.requireActivity(), json, false, null)
        }
        bind.sdkAppInvite.setOnClickListener { OkMyApi.ok.performAppInvite(this.requireActivity()) }
        bind.sdkAppSuggest.setOnClickListener {
            OkMyApi.ok.performAppSuggest(
                this.requireActivity(),
                null
            )
        }
        bind.sdkReportPayment.setOnClickListener {
            OkMyApi.ok.reportPayment(
                Math.random().toString() + "",
                "6.28",
                Currency.getInstance("EUR")
            )
        }

        OkMyApi.ok = OkMyApi.getOk(application)
        OkMyApi.ok.checkValidTokens(ContextOkListener(this.requireContext(),
            onSuccess = { _, _ -> showAppData() },
            onError = { _, err -> toast(getString(R.string.error) + ": $err") }
        ))
        setHasOptionsMenu(true)

        return bind.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }

    private fun showAppData(loggedIn: Boolean = true) {
/*        sdk_form.visibility = if (loggedIn) VISIBLE else GONE
        login_block.visibility = if (loggedIn) GONE else VISIBLE*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            Odnoklassniki.of(this.requireContext()).isActivityRequestOAuth(requestCode) -> {
                // process OAUTH sign-in response
                Odnoklassniki.of(this.requireContext()).onAuthActivityResult(requestCode,
                    resultCode,
                    data,
                    ContextOkListener(this.requireContext(),
                        onSuccess = { _, json ->
                            try {
/*                                toast(String.format("access_token: %s",
                                    json.getString("access_token")))*/
                                //showAppData()
                                navigateToProfile()
                            } catch (e: JSONException) {
                                toast("unable to parse login request ${e.message}")
                            }
                        },
                        onError = { _, err -> toast(getString(R.string.error) + ": $err") },
                        onCancel = { _, err -> toast(getString(R.string.auth_cancelled) + ": $err") }
                    ))
            }
            Odnoklassniki.of(this.requireContext()).isActivityRequestViral(requestCode) -> {
                // process called viral widgets (suggest / invite / post)
                Odnoklassniki.of(this.requireContext()).onActivityResultResult(requestCode,
                    resultCode,
                    data,
                    ContextOkListener(this.requireContext(),
                        onSuccess = { _, json ->
                            toast(json.toString())

                        },
                        onError = { _, err -> toast(getString(R.string.error) + ": $err") }
                    ))
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun navigateToProfile() {
        //создаем действие перехода на другой фрагмент

        val action =
            LoginDirections
                .actionLoginToProfile()
        //action.jsonProfile = json.toString()
        //находим ФиндКонтроллер и запускаем действие
        view?.findNavController()?.navigate(action)
    }

    private fun toast(text: String) =
        Toast.makeText(this.requireContext(), text, Toast.LENGTH_LONG).show()


}

