# homecraft-backend
Platform connecting house owners with construction/finishing professionals


---

## 📚 API Documentation

### Authentication APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login user | ❌ |

### Professional APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/professionals` | Get all professionals | ❌ |
| GET | `/api/professionals/{id}` | Get professional by ID | ❌ |
| GET | `/api/professionals/me` | Get my profile | ✅ |
| PUT | `/api/professionals/profile` | Update profile | ✅ |
| PUT | `/api/professionals/availability` | Update availability | ✅ |
| POST | `/api/professionals/search` | Search professionals | ❌ |

### Project APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/projects` | Create project | ✅ |
| GET | `/api/projects` | Get all projects | ❌ |
| GET | `/api/projects/{id}` | Get project by ID | ❌ |
| PUT | `/api/projects/{id}` | Update project | ✅ |
| PUT | `/api/projects/{id}/complete` | Complete project | ✅ |
| POST | `/api/projects/search` | Search projects | ❌ |

### Bid APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/bids/project/{projectId}` | Submit bid | ✅ |
| GET | `/api/bids/project/{projectId}` | Get bids by project | ❌ |
| PUT | `/api/bids/{id}/accept` | Accept bid | ✅ |
| PUT | `/api/bids/{id}/reject` | Reject bid | ✅ |

### Review APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/reviews/project/{projectId}` | Add review | ✅ |
| GET | `/api/reviews/professional/{id}` | Get reviews by professional | ❌ |
| GET | `/api/reviews/project/{projectId}` | Get reviews by project | ❌ |

### Portfolio APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/portfolio` | Add portfolio item | ✅ |
| GET | `/api/portfolio/professional/{id}` | Get portfolio | ❌ |
| PUT | `/api/portfolio/{id}` | Update portfolio | ✅ |
| DELETE | `/api/portfolio/{id}` | Delete portfolio | ✅ |

---

## 🗄️ Database Schema

### Core Tables

```sql
-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Professionals Table
CREATE TABLE professionals (
    id BIGINT PRIMARY KEY REFERENCES users(id),
    professional_type VARCHAR(20) NOT NULL,
    years_experience INTEGER,
    hourly_rate DECIMAL(10,2),
    bio TEXT,
    location VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    rating_average DECIMAL(3,2) DEFAULT 0,
    total_reviews INTEGER DEFAULT 0,
    verification_status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Projects Table
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    project_type VARCHAR(20) NOT NULL,
    professional_type_needed VARCHAR(20) NOT NULL,
    budget_min DECIMAL(10,2),
    budget_max DECIMAL(10,2),
    location VARCHAR(255),
    status VARCHAR(20) DEFAULT 'OPEN',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Bids Table
CREATE TABLE bids (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    professional_id BIGINT NOT NULL REFERENCES professionals(id),
    bid_amount DECIMAL(10,2) NOT NULL,
    estimated_days INTEGER,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Reviews Table
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    client_id BIGINT NOT NULL REFERENCES clients(id),
    professional_id BIGINT NOT NULL REFERENCES professionals(id),
    rating INTEGER CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP
);

-- Portfolio Items Table
CREATE TABLE portfolio_items (
    id BIGSERIAL PRIMARY KEY,
    professional_id BIGINT NOT NULL REFERENCES professionals(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500) NOT NULL,
    project_type VARCHAR(100),
    created_at TIMESTAMP
);
