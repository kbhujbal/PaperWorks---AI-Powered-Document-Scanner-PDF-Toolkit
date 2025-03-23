# 📄 PaperWorks - AI-Powered Document Scanner & PDF Toolkit

PaperWorks is a feature-rich Android application designed to streamline your document handling process. It combines document scanning, editing, PDF tools, OCR (Optical Character Recognition), and QR code utilities into one easy-to-use, ad-free platform — entirely free of charge.

---

## ✨ Features

### 📷 Document Scanner
- High-resolution scanning
- Auto edge detection and cropping
- Custom filters and enhancements
- Add signatures, watermarks, text, and highlights

### 🛠 PDF Tools
- Merge and split PDFs
- Add/remove watermarks and passwords
- Reorder or remove pages
- Extract images from PDFs

### 🔍 OCR (Image to Text)
- Extract editable text from images
- Supports multiple font styles and layouts

### 📱 QR Code Tools
- Generate QR codes for text, email, phone, SMS, or URLs
- Scan QR codes to retrieve embedded information

---

## 📦 Technologies Used

- **Android SDK**
- **Java**
- **SQLite**
- **OpenCV** for image processing
- **Smart Cropper** for intelligent cropping
- **CameraView** for camera interface
- **PhotoEditor** for image editing
- **PDFViewer** for PDF rendering
- **iText** for PDF manipulation

---

## 🏗️ Project Structure

```
PaperWorks/
├── app/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── paperworks/
│   │               ├── activities/
│   │               ├── database/
│   │               ├── ocr/
│   │               ├── pdftools/
│   │               ├── qr/
│   │               ├── scanner/
│   │               └── utils/
│   └── res/
│       ├── layout/
│       ├── drawable/
│       └── values/
└── AndroidManifest.xml
```

---

## 🚀 How to Use

1. Clone the repository:
   ```bash
   git clone https://github.com/kbhujbal/PaperWorks---AI-Powered-Document-Scanner.git
   ```

2. Open the project in **Android Studio**.

3. Connect an Android device or use an emulator.

4. Build and run the app.

---

## 🔐 Permissions Required

- Camera Access
- Read/Write External Storage
- Internet (optional for sharing features)

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🔭 Future Scope

- Cloud storage integration (Google Drive, Dropbox)
- Multi-language OCR support
- Web version and cross-platform support
- AI-powered scan enhancement

