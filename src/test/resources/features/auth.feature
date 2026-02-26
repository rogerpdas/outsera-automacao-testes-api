# language: pt
@auth
Funcionalidade: Autenticação - POST /auth/login e GET /auth/me

  Contexto:
    Dado que o sistema está configurado com a URL base "https://dummyjson.com"
  # ===================== LOGIN =====================

  @smoke @positivo
  Cenário: Login com credenciais válidas retorna token JWT
    Quando realizo login com usuário "emilys" e senha "emilyspass"
    Então o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "auth-login-schema.json"
    E o header "Content-Type" deve conter "application/json"
    E o corpo da resposta deve conter o campo "accessToken"
    E o token JWT é armazenado para uso posterior

  @negativo
  Cenário: Login com senha incorreta retorna erro de autenticação
    Quando realizo login com usuário "emilys" e senha "senhaerrada"
    Então o status code da resposta deve ser 400

  @negativo
  Cenário: Login com username ausente retorna erro de validação
    Quando realizo login com a senha "emilyspass" e titulo nulo
    Então o status code da resposta deve ser 400

  @negativo
  Cenário: Login com payload malformado retorna erro
    Quando realizo uma requisição POST para "/auth/login" com corpo malformado "nao-e-json"
    Então o status code da resposta deve ser 400
  # ===================== AUTH ME =====================

  @smoke @positivo
  Cenário: Buscar perfil autenticado com token válido retorna dados do usuário
    Dado que estou autenticado com credenciais válidas
    Quando busco meu perfil logado
    Então o status code da resposta deve ser 200
    E o header "Content-Type" deve conter "application/json"
    E o corpo da resposta deve conter o campo "username"
    E o campo "username" da resposta deve ser "emilys"

  @negativo
  Cenário: Buscar perfil sem token retorna erro de autenticação
    Quando busco meu perfil logado sem token
    Então o status code da resposta deve ser 401

  @negativo
  Cenário: Buscar perfil com token inválido retorna erro de autenticação
    Quando busco meu perfil logado com token inválido "Bearer token_invalido_xyz"
    Então o status code da resposta deve ser 401
