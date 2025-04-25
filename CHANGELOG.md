# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Token refresh functionality in OAuth2HeaderInterceptor
- Support for EVETokenExpiry cookie in DataFetchController
- Automatic token refresh when expired tokens are detected
- Proper error handling for token refresh operations

### Changed
- Modified OAuth2HeaderInterceptor to retrieve tokens from AuthTokenRepository
- Enhanced DataFetchService to convert ISO-8601 expiry time to ZonedDateTime
- Updated token storage to include expiry time information
- Improved logging for token-related operations

### Fixed
- Issue with expired tokens causing API requests to fail
- Token refresh mechanism to maintain continuous authentication

## [1.0.0] - 2024-06-09

### Added
- Initial release of vs-industry application
- Support for EVE Online ESI API integration
- Authentication using OAuth2 with EVE Online
- Data fetching for industry jobs, journal entries, and market transactions
- Token storage in database for persistent authentication