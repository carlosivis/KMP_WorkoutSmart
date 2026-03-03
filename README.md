# WorkoutSmart 💪

O **WorkoutSmart** é um aplicativo de acompanhamento de treinos multiplataforma (Android e iOS) desenvolvido 100% com **Kotlin Multiplatform (KMP)** e **Compose Multiplatform**. 

Construído com foco em performance e organização, o app permite que os usuários gerenciem suas rotinas de academia, registrem seus históricos de treinos de forma *offline-first* e interajam com amigos através de um sistema gamificado de grupos e rankings.

---

## 🔗 Backend API
Este aplicativo consome uma API dedicada para a sincronização de dados sociais e autenticação. O código-fonte do servidor e as instruções para rodá-lo localmente podem ser encontrados no repositório abaixo:
👉 **[WorkoutSmartBackend](https://github.com/carlosivis/WorkoutSmartBackend)**

---

## ✨ Funcionalidades (Features)

* **Autenticação Segura:** Login unificado com Google utilizando **Firebase Authentication**, sincronizado com o backend próprio para geração de tokens de acesso.
* **Gestão de Treinos Offline-First:** * Crie, edite e exclua treinos e exercícios.
  * Os dados são salvos localmente utilizando **SQLDelight**, garantindo que o app funcione perfeitamente sem internet.
* **Execução de Treino:** Interface dedicada para treinos ativos, contando com cronômetro de descanso (`TimerPicker`) e registro automático de duração.
* **Histórico de Atividades:** Acompanhamento automático da data e duração dos treinos finalizados.
* **Recursos Sociais e Gamificação:** * Crie grupos e convide amigos via código.
  * Visualize rankings dinâmicos de membros baseados na consistência e conclusão de treinos.
* **Suporte a Temas:** Alternância entre Light e Dark mode.

---

## 🏗️ Arquitetura e Padrões

O projeto foi rigorosamente estruturado seguindo os princípios da **Clean Architecture**, garantindo alta testabilidade, separação de responsabilidades e escalabilidade.

* **Camada de Apresentação (UI):** Padrão MVI/MVVM utilizando `ViewState`, `ViewAction` e `ViewModel`.
* **Camada de Domínio (Domain):** Isolamento de regras de negócio em `UseCases` (ex: `LoginGoogleUseCase`, `CreateGroupUseCase`) e contratos de repositórios.
* **Camada de Dados (Data):** Padrão Repository e isolamento de fontes de dados (`LocalDataSource` e `RemoteDataSource`).

### 🛠️ Tech Stack

* **UI:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-mpp/)
* **Navegação & Ciclo de Vida:** [Decompose](https://arkivanov.github.io/Decompose/)
* **Injeção de Dependências:** [Koin](https://insert-koin.io/)
* **Banco de Dados Local:** [SQLDelight](https://cashapp.github.io/sqldelight/)
* **Networking HTTP:** [Ktor Client](https://ktor.io/)
* **Concorrência:** Kotlin Coroutines & Flows
* **Autenticação:** Firebase Auth + Provedores Nativos
* **Data/Hora:** Kotlinx Datetime
* **Serialização:** Kotlinx Serialization

---

## 📂 Estrutura do Projeto

A base de código está dividida da seguinte forma para maximizar o compartilhamento:

```text
├── composeApp/
│   ├── src/androidMain/     # Código específico para Android (Provedores nativos, MainActivity)
│   ├── src/iosMain/         # Código específico para iOS (MainViewController, bindings)
│   └── src/commonMain/      # Código 100% compartilhado (UI, Domain, Data, DI)
│       ├── core/            # Utilitários, Ktor Client, Wrappers de Network
│       ├── data/            # Implementações de Repositórios, Data Sources, SQLDelight
│       ├── domain/          # Interfaces de Repositório, UseCases
│       ├── models/          # Entidades, Responses, Requests
│       ├── navigation/      # Componentes de navegação do Decompose
│       └── screens/         # ViewModels, Estados, Ações e Telas em Compose
├── iosApp/                  # Projeto nativo do Xcode
└── backend/                 # (Link externo)

```

---

## 🚀 Configuração do Ambiente (Setup)

Para rodar o projeto localmente, é estritamente necessário configurar o Firebase e apontar para o seu backend.

### Pré-requisitos

* Android Studio (Ladybug ou superior recomendado).
* Xcode (para build iOS).
* Plugin do Kotlin Multiplatform instalado no Android Studio.

### 1. Configuração do Firebase (Obrigatório)

Crie um projeto no [Firebase Console](https://console.firebase.google.com/) com a autenticação do Google ativada e registre os aplicativos Android e iOS.

* **Para Android:**
1. Baixe o arquivo `google-services.json`.
2. Adicione o arquivo no diretório: `composeApp/androidApp/src/main/` (ou onde estiver seu diretório raiz Android).


* **Para iOS:**
1. Baixe o arquivo `GoogleService-Info.plist`.
2. Abra o projeto iOS no Xcode (diretório `iosApp/`) e arraste o arquivo para dentro da raiz do target principal do projeto.



### 2. Configuração da API

O aplicativo utiliza o `BuildConfig.BASE_URL` para se conectar ao backend. Certifique-se de configurar essa constante no arquivo `build.gradle.kts` ou `local.properties` para apontar para o servidor (ex: `http://localhost:8080` ou a URL de produção).

### 3. Executando o App

* **Android:** Selecione o target `composeApp` no Android Studio e clique em *Run* em um emulador ou dispositivo físico.
* **iOS:** Abra o arquivo `.xcworkspace` em `iosApp/` pelo Xcode e execute em um simulador, ou use a configuração *iOS Simulator* no Fleet/Android Studio.

---

## 🤝 Contribuição

Este é um projeto de portfólio pessoal e está em constante evolução. Feedbacks e sugestões sobre a arquitetura (especialmente KMP e Koin) são sempre bem-vindos! Sinta-se à vontade para abrir uma *Issue* ou enviar um *Pull Request*.

```
