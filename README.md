##Firebase Authentication and filter input text editing
### Output
<p float="left">
  <img src="https://github.com/sumber-rejeki/TKM13-Login-RegisterAuth/blob/d944db6fa351145f548f8c2cdbb5b97caf4beb6c/UII.png?raw=true" alt="ui"/>
</p>

```javascript
        //Fullname validation
        val nameStream = RxTextView.textChanges(binding.etFullname)
            .skipInitialValue()
            .map{name ->
                name.trim().isEmpty()
            }
        nameStream.subscribe {
            showNameExistAlert(it)
        }

        //Email validation
        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe{
            showEmailValidAlert(it)
        }

        //Username validation
        val usernameStream = RxTextView.textChanges(binding.etUsername)
            .skipInitialValue()
            .map { username ->
                val usernameStr = username.toString().trim()
                usernameStr.isEmpty() || usernameStr.length < 6
            }
        usernameStream.subscribe{
            showTextMinimalAlert(it, "Username")
        }

        //Password validation
        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                val passwordStr = password.toString()
                val hasUpperCase = passwordStr.any { it.isUpperCase() }
                val hasSymbol = passwordStr.any { !it.isLetterOrDigit() }
                val isValidLength = passwordStr.length >= 8
                !isValidLength || !hasUpperCase || !hasSymbol
            }
        passwordStream.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        //Confirm password validation
        val passwordConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.etConfirmPassword)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.etConfirmPassword.text.toString()
                },
            RxTextView.textChanges(binding.etConfirmPassword)
                .skipInitialValue()
                .map { confirmPassword ->
                    confirmPassword.toString() != binding.etPassword.text.toString()
                })
        passwordConfirmStream.subscribe {
            showPasswordConfirm(it)
        }

        //Button Enabled true or false
        val invalidFieldStream = Observable.combineLatest(
            nameStream,
            emailStream,
            usernameStream,
            passwordStream,
            passwordConfirmStream,
            {nameInvalid: Boolean, emailInvalid: Boolean, usernameInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ->
                !nameInvalid && !emailInvalid && !usernameInvalid && !passwordInvalid && !passwordConfirmInvalid
            })
        invalidFieldStream.subscribe { isValid ->
            if(isValid){
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            }else{
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        //Click button
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            registerUser(email, password)
        }
        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun showNameExistAlert(isNotValid: Boolean){
        binding.etFullname.error = if (isNotValid) "Nama tidak boleh kosong!" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text:String){
        if (text == "Username")
            binding.etUsername.error = if (isNotValid) "$text tidak boleh kosong dan harus minimal 6 huruf!" else null
        else if (text == "Password")
            binding.etPassword.error = if (isNotValid) "$text harus lebih dari 8 huruf, mengandung 1 huruf besar dan 1 simbol!" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean){
        binding.etEmail.error = if (isNotValid) "Email tidak valid!" else  null
    }

    private fun showPasswordConfirm(isNotValid: Boolean){
        binding.etConfirmPassword.error = if (isNotValid) "Password tidak valid!" else null
    }
```
