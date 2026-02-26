# language: pt
@products
Funcionalidade: Produtos - CRUD completo em /products

  Contexto:
    Dado que o sistema está configurado com a URL base "https://dummyjson.com"
    E que estou autenticado com credenciais válidas
  # ===================== GET /auth/products =====================

  @smoke @positivo
  Cenário: Listar todos os produtos com token válido
    Quando listo todos os produtos
    Então o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "products-list-schema.json"
    E o header "Content-Type" deve conter "application/json"
    E o corpo da resposta deve conter o campo "products"
    E o campo "total" da resposta deve ser maior que 0

  @ignore @negativo
  Cenário: Listar produtos sem token retorna erro de autenticação
    Quando listo todos os produtos sem token
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Listar produtos com token inválido retorna erro
    Quando listo todos os produtos com token inválido "Bearer abc123invalid"
    Então o status code da resposta deve ser 401
  # ===================== GET /auth/products/{id} =====================

  @smoke @positivo
  Cenário: Buscar produto por ID válido retorna produto
    Quando busco o produto de ID 1
    Então o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "products-single-schema.json"
    E o header "Content-Type" deve conter "application/json"
    E o corpo da resposta deve conter o campo "id"
    E o corpo da resposta deve conter o campo "title"
    E o corpo da resposta deve conter o campo "price"

  @ignore @negativo
  Cenário: Buscar produto com ID inexistente retorna 404
    Quando busco o produto de ID 99999
    Então o status code da resposta deve ser 404

  @ignore @negativo
  Cenário: Buscar produto sem token retorna erro de autenticação
    Quando busco o produto de ID 1 sem token
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Buscar produto com token inválido retorna erro
    Quando busco o produto de ID 1 com token inválido "Bearer invalido"
    Então o status code da resposta deve ser 401
  # ===================== POST /auth/products/add =====================

  @smoke @positivo
  Cenário: Criar produto com dados válidos retorna produto criado
    Quando eu crio um novo produto com titulo "Produto Teste Automação" e preço 99.99
    Então o status code da resposta deve ser 201
    E o contrato da resposta deve respeitar o schema "products-single-schema.json"
    E o header "Content-Type" deve conter "application/json"
    E o corpo da resposta deve conter o campo "id"

  @ignore @negativo
  Cenário: Criar produto sem token retorna erro de autenticação
    Quando eu crio um produto sem token
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Criar produto com token inválido retorna erro
    Quando eu crio um produto com token inválido "Bearer xpto"
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Criar produto com price como string retorna erro de validação
    Quando eu crio um produto enviando preco como string
    Então o status code da resposta deve ser 400
  # ===================== PATCH /auth/products/{id} =====================

  @smoke @positivo
  Cenário: Atualizar parcialmente produto existente com dados válidos
    Quando atualizo o produto de ID 1 mudando o titulo para "Produto Atualizado Automação"
    Então o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "products-single-schema.json"
    E o header "Content-Type" deve conter "application/json"

  @ignore @negativo
  Cenário: Atualizar produto sem token retorna erro de autenticação
    Quando atualizo o produto de ID 1 sem token
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Atualizar produto com token inválido retorna erro
    Quando atualizo o produto de ID 1 com token inválido "Bearer fake"
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Atualizar produto com ID inexistente retorna 404
    Quando atualizo o produto de ID 99999 mudando o titulo para "Produto Inexistente"
    Então o status code da resposta deve ser 404
  # ===================== DELETE /auth/products/{id} =====================

  @smoke @positivo
  Cenário: Deletar produto existente retorna sucesso
    Quando deleto o produto de ID 1
    Então o status code da resposta deve ser 200
    E o header "Content-Type" deve conter "application/json"

  @ignore @negativo
  Cenário: Deletar produto sem token retorna erro de autenticação
    Quando deleto o produto de ID 1 sem token
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Deletar produto com token inválido retorna erro
    Quando deleto o produto de ID 1 com token inválido "Bearer invalido"
    Então o status code da resposta deve ser 401

  @ignore @negativo
  Cenário: Deletar produto com ID inexistente retorna 404
    Quando deleto o produto de ID 99999
    Então o status code da resposta deve ser 404
