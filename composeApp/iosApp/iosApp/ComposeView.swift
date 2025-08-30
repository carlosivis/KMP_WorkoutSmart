import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        setupKoin()
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    private func setupKoin() {
        let koin = KoinKt.doInitKoin().koin
    }
}

