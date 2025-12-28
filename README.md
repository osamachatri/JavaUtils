# 🚀 Java Utility Hub

<div align="center">

[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/osamachatri/JavaUtils/graphs/commit-activity)

**A comprehensive, production-ready collection of Java utility classes for everyday development**

[Features](#-features) • [Getting Started](#-getting-started) • [Documentation](#-documentation) • [Contributing](#-contributing) • [License](#-license)

</div>

---

## 📖 Overview

**JavaUtils** is a carefully curated toolbox of reusable, well-tested utility classes designed to accelerate Java development. From string manipulation and cryptography to networking and reflection — everything you need is organized, documented, and ready to integrate into your projects.

### Why JavaUtils?

- ✨ **Production-Ready** — Battle-tested utilities for real-world applications
- 🧪 **Well-Tested** — High test coverage for reliability and confidence
- 📚 **Comprehensive** — Covers common tasks across multiple domains
- 🎯 **Zero Dependencies** — Pure Java implementations (JDK 8+)
- 🛠️ **Clean Architecture** — Modular design for easy integration
- 🚀 **Developer-Friendly** — Clear documentation and intuitive APIs

---

## 📦 Utility Modules

### Core Utilities

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 🔤 **String Utils** | String manipulation, trimming, validation, case conversion | ✅ Ready | `StringUtils.reverse("hello")` |
| 🧮 **Math Utils** | Advanced calculations, rounding, statistics, percentage | ✅ Ready | `MathUtils.percentage(25, 100)` |
| 📅 **Date & Time Utils** | Formatting, parsing, calculations, timezone handling | ✅ Ready | `DateUtils.daysBetween(date1, date2)` |
| 🧰 **Collection Utils** | Safe operations, transformations, filtering, deep copy | ✅ Ready | `CollectionUtils.safeGet(list, index)` |

### Data & Validation

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 🧪 **Validation Utils** | Email, phone, URL, password strength validators | ✅ Ready | `ValidationUtils.isValidEmail(email)` |
| 📊 **JSON Utils** | Parse, serialize, pretty print, path extraction | 🚧 In Progress | `JsonUtils.parse(jsonString)` |
| 🔍 **XML Utils** | Parse, transform, validate XML documents | 📋 Planned | `XmlUtils.parseXml(xmlString)` |
| 📈 **CSV Utils** | Read, write, parse CSV files with headers | 📋 Planned | `CsvUtils.readCsv(file)` |

### I/O & System

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 📂 **File Utils** | Read, write, copy, delete, metadata operations | ✅ Ready | `FileUtils.readFile(path)` |
| 💾 **IO Utils** | Stream handling, buffering, resource cleanup | 🚧 In Progress | `IOUtils.copyStream(in, out)` |
| ⚙️ **System Utils** | Environment variables, OS detection, process management | 📋 Planned | `SystemUtils.getOsName()` |
| 📋 **Properties Utils** | Load, save, merge configuration properties | 📋 Planned | `PropsUtils.load(file)` |

### Security & Encoding

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 🔐 **Crypto Utils** | Hashing (SHA, MD5), encryption (AES), password handling | ✅ Ready | `CryptoUtils.sha256(input)` |
| 🔑 **JWT Utils** | Generate, validate, parse JSON Web Tokens | 📋 Planned | `JwtUtils.generate(claims)` |
| 🎯 **Encoding Utils** | Base64, URL encoding, hex conversion | 🚧 In Progress | `EncodingUtils.base64Encode(data)` |

### Advanced Features

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 🔍 **Reflection Utils** | Dynamic field access, method invocation, introspection | ✅ Ready | `ReflectionUtils.getFieldValue(obj, "id")` |
| 🔗 **Network Utils** | HTTP client, URL validation, IP operations, pinging | ✅ Ready | `NetworkUtils.pingUrl(url)` |
| 🧵 **Concurrency Utils** | Thread pools, locks, executors, async operations | 🚧 In Progress | `ConcurrencyUtils.runAsync(task)` |
| 🔄 **Retry Utils** | Configurable retry logic with backoff strategies | 📋 Planned | `RetryUtils.withRetry(callable, 3)` |
| 🎭 **Proxy Utils** | Dynamic proxy creation, AOP-style interception | 📋 Planned | `ProxyUtils.createLoggingProxy(obj)` |

### Data Structures

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 📦 **Cache Utils** | In-memory caching with TTL, LRU eviction | 🚧 In Progress | `CacheUtils.put(key, value, ttl)` |
| 🗂️ **Tree Utils** | Tree traversal, manipulation, binary tree operations | 📋 Planned | `TreeUtils.traverse(root, order)` |
| 📊 **Graph Utils** | Graph algorithms, shortest path, cycle detection | 📋 Planned | `GraphUtils.shortestPath(graph, a, b)` |

### Specialized

| Module | Description | Status | Quick Example |
|--------|-------------|--------|---------------|
| 🖼️ **Image Utils** | Resize, crop, format conversion, metadata | 📋 Planned | `ImageUtils.resize(image, width, height)` |
| 📧 **Email Utils** | Send emails, validate formats, template handling | 📋 Planned | `EmailUtils.send(to, subject, body)` |
| 🌐 **URL Utils** | Parse, build, validate URLs with parameters | 🚧 In Progress | `UrlUtils.buildUrl(base, params)` |
| 🎲 **Random Utils** | Secure random generation, UUID, strings | 📋 Planned | `RandomUtils.randomString(10)` |
| 🔢 **Number Utils** | Parsing, formatting, range checks, rounding | 📋 Planned | `NumberUtils.isInRange(n, min, max)` |

**Legend**: ✅ Ready | 🚧 In Progress | 📋 Planned

---

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven or Gradle (optional, for build automation)

### Installation

#### Option 1: Clone the Repository

```bash
git clone https://github.com/osamachatri/JavaUtils.git
cd JavaUtils
```

#### Option 2: Download Specific Utilities

Simply copy the utility classes you need into your project's source directory.

### Quick Start Example

```java
import com.yourpackage.utils.*;

public class QuickStartExample {
    public static void main(String[] args) {
        // String manipulation
        String reversed = StringUtils.reverse("Hello World");
        
        // Validation
        boolean isValid = ValidationUtils.isValidEmail("user@example.com");
        
        // Cryptography
        String hashed = CryptoUtils.sha256("password");
        
        // File operations
        String content = FileUtils.readFile("config.txt");
        
        // Network operations
        boolean isUp = NetworkUtils.pingUrl("https://google.com");
        
        // Collections
        List<String> safe = CollectionUtils.safeSubList(list, 0, 10);
    }
}
```

---

## 📚 Documentation

### Module Documentation

Each utility module includes:
- **Javadoc comments** — Detailed inline documentation
- **Usage examples** — Real-world code samples
- **Unit tests** — Demonstrating expected behavior

### API Reference

Browse the [Wiki](https://github.com/osamachatri/JavaUtils/wiki) for comprehensive API documentation and tutorials.

### Best Practices

1. **Null Safety** — Most methods handle null inputs gracefully
2. **Exception Handling** — Clear exceptions with descriptive messages
3. **Thread Safety** — Documented where applicable
4. **Performance** — Optimized for common use cases

---

## 🧪 Testing

All utilities are thoroughly tested using JUnit 5.

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=StringUtilsTest
```

**Test Coverage**: We aim for >80% code coverage across all modules.

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Ways to Contribute

- 🐛 **Report bugs** — Open an issue describing the problem
- ✨ **Suggest features** — Share your ideas for new utilities
- 📝 **Improve documentation** — Help others understand the code
- 🔧 **Submit pull requests** — Add new features or fix bugs

### Contribution Guidelines

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Write tests** for your changes
4. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
5. **Push** to the branch (`git push origin feature/AmazingFeature`)
6. **Open** a Pull Request

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

---

## 📋 Roadmap

### Q1 2025
- ✅ Complete Reflection Utils (Basic & Advanced)
- ✅ Complete Network Utils (Basic & Advanced)
- 🚧 Complete Concurrency Utils
- 📋 Add JSON Utils
- 📋 Add Cache Utils

### Q2 2025
- 📋 Add Retry Utils with exponential backoff
- 📋 Add Graph & Tree utilities
- 📋 Add Image processing utilities
- 📋 Performance benchmarks and optimizations

### Future
- 📋 Reactive programming utilities
- 📋 Database connection utilities
- 📋 Monitoring and metrics utilities
- 📋 Cloud service integration helpers

See the [open issues](https://github.com/osamachatri/JavaUtils/issues) for a full list of proposed features and known issues.

---

## 🏗️ Project Structure

```
JavaUtils/
├── src/
│   ├── main/java/com/utils/
│   │   ├── string/          # String utilities
│   │   ├── validation/      # Validation utilities
│   │   ├── date/            # Date & time utilities
│   │   ├── math/            # Math utilities
│   │   ├── collection/      # Collection utilities
│   │   ├── file/            # File utilities
│   │   ├── crypto/          # Cryptography utilities
│   │   ├── reflection/      # Reflection utilities
│   │   └── network/         # Network utilities
│   └── test/java/           # Unit tests
├── docs/                    # Documentation
├── examples/                # Usage examples
├── README.md
├── CONTRIBUTING.md
└── LICENSE
```

---

## 📄 License

"
This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) - see the [LICENSE](LICENSE) file for details.
---

## 🙏 Acknowledgments

- Inspired by [Apache Commons](https://commons.apache.org/) and Google Guava
- Thanks to all contributors who help improve this library
- Community feedback and suggestions are invaluable

---

## 📞 Contact & Support

- **Issues**: [GitHub Issues](https://github.com/osamachatri/JavaUtils/issues)
- **Discussions**: [GitHub Discussions](https://github.com/osamachatri/JavaUtils/discussions)
- **Email**: oussamachatri7@gmail.com

---

## ⭐ Star History

If you find this project useful, please consider giving it a star! It helps others discover the project.

[![Star History Chart](https://api.star-history.com/svg?repos=osamachatri/JavaUtils&type=Date)](https://star-history.com/#osamachatri/JavaUtils&Date)

---

<div align="center">

**Made with ❤️ by developers, for developers**

[⬆ Back to Top](#-java-utility-hub)

</div>
